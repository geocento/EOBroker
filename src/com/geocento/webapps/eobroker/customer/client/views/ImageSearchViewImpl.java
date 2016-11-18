package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.styles.MyDataGridResources;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.*;
import com.geocento.webapps.eobroker.common.client.widgets.table.celltable.SubrowTableBuilder;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.ImageService;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.imageapi.Product;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.styles.StyleResources;
import com.geocento.webapps.eobroker.customer.client.widgets.MaterialCheckBoxCell;
import com.geocento.webapps.eobroker.customer.client.widgets.MaterialSensorsSuggestion;
import com.geocento.webapps.eobroker.customer.client.widgets.maps.MapContainer;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import gwt.material.design.client.base.MaterialImageCell;
import gwt.material.design.client.constants.ImageType;
import gwt.material.design.client.ui.*;

import java.util.*;

/**
 * Created by thomas on 09/05/2016.
 */
public class ImageSearchViewImpl extends Composite implements ImageSearchView, ResizeHandler {

    interface SearchPageUiBinder extends UiBinder<Widget, ImageSearchViewImpl> {
    }

    private static SearchPageUiBinder ourUiBinder = GWT.create(SearchPageUiBinder.class);

    static public interface Style extends CssResource {
        String navOpened();
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialDropDown providerDropdown;
    @UiField
    MaterialLink providersLink;
    @UiField
    MapContainer mapContainer;
    @UiField
    MaterialTab tab;
    @UiField
    MaterialSensorsSuggestion sensors;
    @UiField
    MaterialDatePicker startDate;
    @UiField
    MaterialDatePicker stopDate;
    @UiField
    SimplePanel resultsPanel;
    @UiField
    MaterialRow queryPanel;
    @UiField
    MaterialButton update;
    @UiField
    MaterialSideNav searchBar;
    @UiField
    HTMLPanel mapPanel;
    @UiField
    MaterialButton submitForQuote;

    private Presenter presenter;

    private Product outlinedProduct;

    private GraphicJSNI outlinedProductGraphicJSNI;

    private DataGrid<Product> resultsTable;

    private ListDataProvider<Product> imagesList;

    private ColumnSortEvent.ListHandler<Product> sortDataHandler;

    private HashSet<Product> selectedProducts = new HashSet<Product>();

    private class ProductRendering {
        GraphicJSNI footprint;
        WMSLayerJSNI overlay;
    }

    private HashMap<Product, ProductRendering> renderedProducts = new HashMap<Product, ProductRendering>();

    private static NumberFormat format = NumberFormat.getFormat("#.##");

    public ImageSearchViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        tab.setBackgroundColor("teal lighten-2");
        //tab.setIndicatorColor("yellow");

        startDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                presenter.onStartDateChanged(event.getValue());
            }
        });
        stopDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                presenter.onStopDateChanged(event.getValue());
            }
        });
        sensors.setPresenter(new MaterialSensorsSuggestion.Presenter() {
            @Override
            public void onTextChanged(String text) {
                presenter.onSensorsChanged(text);
            }
        });
        createResultsTable();

        mapContainer.setPresenter(new MapContainer.Presenter() {
            @Override
            public void aoiChanged(AoIDTO aoi) {
                displayAoI(aoi);
            }

            @Override
            public void aoiSelected(AoIDTO aoi) {
                displayAoI(aoi);
            }
        });

        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                searchBar.show();
                tab.selectTab("query");
                onResize(null);
            }
        });
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        mapContainer.setMapLoadedHandler(mapLoadedHandler);
    }

    @Override
    public void displayAoI(AoIDTO aoi) {
        mapContainer.displayAoI(aoi);
    }

    @Override
    public void setText(String text) {
        sensors.setText(text);
    }

    @Override
    public void displayLoadingResults(String message) {
        template.setLoading(message);
        searchBar.hide();
        onResize(null);
    }

    @Override
    public void hideLoadingResults() {
        template.hideLoading();
        searchBar.show();
        tab.selectTab("results");
        onResize(null);
    }

    private void createResultsTable() {
        // create table
        resultsTable = new DataGrid<Product>(1000, MyDataGridResources.INSTANCE);
        resultsTable.setSize("100%", "100%");
        // create the list data provider
        imagesList = new ListDataProvider<Product>();
        imagesList.setList(new ArrayList<Product>());
        imagesList.addDataDisplay(resultsTable);

        resultsTable.addCellPreviewHandler(new CellPreviewEvent.Handler<Product>() {
            @Override
            public void onCellPreview(CellPreviewEvent<Product> event) {
                if (BrowserEvents.MOUSEOVER.equals(event.getNativeEvent().getType())) {
                    outlineProduct(event.getValue());
                } else if (BrowserEvents.MOUSEOUT.equals(event.getNativeEvent().getType())) {
                    outlineProduct(null);
                }
            }
        });
        sortDataHandler = new ColumnSortEvent.ListHandler<>(imagesList.getList());
        resultsTable.addColumnSortHandler(sortDataHandler);

/*
        resultsTable.setSelectionModel(selectionModel);
*/

        // now add table to the results panel
        resultsPanel.setWidget(resultsTable);

        SubrowTableBuilder tableBuilder = new SubrowTableBuilder<Product>(resultsTable) {
            @Override
            protected String getInformation(Product product) {
                String htmlContent = "";
                htmlContent += addProperty("Resolution", formatNumber(product.getSensorResolution(), "m"));
                htmlContent += addProperty("AoI Coverage", formatNumber(product.getAoiCoveragePercent() * 100, "%"));
                if(product.getOrbit() != null) {
                    htmlContent += addProperty("Orbit", product.getOrbit());
                }
                if(product.getSensorType().startsWith("O")) {
                    htmlContent += addProperty("Cloud cover", formatNumber(product.getCloudCoveragePercent() == -1 ? null : product.getCloudCoveragePercent() * 100, "%"));
                    htmlContent += addProperty("Off-Nadir Angle", formatNumber(product.getOna(), "deg"));
                    htmlContent += addProperty("Illum. Angle", formatNumber(product.getSza(), "deg"));
                    if(product.getStereoStackName() != null) {
                        htmlContent += addProperty("Stereo", "Stack" + product.getStereoStackName());
                    }
                } else {
                    if(product.getOrbitDirection() != null) {
                        htmlContent += addProperty("Orbit Dir.", formatString(product.getOrbitDirection() == Product.ORBITDIRECTION.ASCENDING ? "Ascending" : "Descending"));
                    }
                    htmlContent += addProperty("Polarisation Mode", formatString(product.getPolarisation()));
                    htmlContent += addProperty("Incidence Angle", formatNumber(product.getOza(), "deg"));
                }
                return htmlContent;
            }

            private String formatString(String value) {
                return value == null ?  "NA" : value;
            }

            private String formatNumber(Double value, String unitValue) {
                return value == null || value == -1 ?  "NA" : (format.format(value) + " " + unitValue);
            }

            private String addProperty(String name, Object value) {
                return "<span style='padding: 5px;'><b>" + name + ": </b>" + (value == null ? "NA" : value.toString()) + "</span>";
            }

        };
        resultsTable.setTableBuilder(tableBuilder);

        // define columns
        Column<Product, Boolean> checkColumn = new Column<Product, Boolean>(new MaterialCheckBoxCell()) {
            @Override
            public Boolean getValue(Product product) {
                return selectedProducts.contains(product);
            }
        };
        checkColumn.setFieldUpdater(new FieldUpdater<Product, Boolean>() {
            @Override
            public void update(int index, Product product, Boolean value) {
                if(selectedProducts.contains(product) != value) {
                    if(value) {
                        selectedProducts.add(product);
                    } else {
                        selectedProducts.remove(product);
                    }
                    resultsTable.redraw();
                    refreshMap();
                    enableQuotingMayBe();
                }
            }
        });

        // IMAGE
        Column<Product, MaterialImage> thumbnailColumn = new Column<Product, MaterialImage>(new MaterialImageCell()) {
            @Override
            public MaterialImage getValue(Product object) {
                MaterialImage img = new MaterialImage();
                img.setUrl(object.getThumbnail() == null ?
                                (object.getType() == Product.TYPE.ARCHIVE ? "./images/imageryNotAvailable.png" :
                                        object.getType() == Product.TYPE.PLANNEDACQ ? "./images/imageryPlannedAcq.png" :
                                        "./images/imageryFutureAcq.png") :
                        object.getThumbnail());
                img.setWidth("40px");
                img.setHeight("40px");
                //SET IMAGE TO CIRCLE
                img.setType(ImageType.DEFAULT);
                return img;
            }
        };
        TextColumn<Product> sensorColumn = new TextColumn<Product>() {
            @Override
            public String getValue(Product object) {
                return object.getSatelliteName();
            }
        };
        sensorColumn.setSortable(true);
        sortDataHandler.setComparator(sensorColumn, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getSatelliteName().compareTo(o2.getSatelliteName());
            }
        });

        // ITEM NAME
        TextColumn<Product> dateColumn = new TextColumn<Product>() {

            private DateTimeFormat fmt = DateTimeFormat.getShortDateFormat();

            @Override
            public String getValue(Product imageProductDTO) {
                return fmt.format(imageProductDTO.getStart());
            }
        };
        dateColumn.setSortable(true);
        sortDataHandler.setComparator(dateColumn, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getStart().compareTo(o2.getStart());
            }
        });

        // set the row styles based on type of product
        resultsTable.setRowStyles(new RowStyles<Product>() {
            @Override
            public String getStyleNames(Product product, int rowIndex) {
                return product.getType() == Product.TYPE.ARCHIVE ? "eobroker-archiveProduct" :
                        product.getType() == Product.TYPE.PLANNEDACQ ? "eobroker-plannedAcqProduct" :
                        "eobroker-futureOpportunityProduct";
            }
        });

        resultsTable.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        resultsTable.setColumnWidth(checkColumn, "30px");
        resultsTable.addColumn(thumbnailColumn, "");
        resultsTable.setColumnWidth(thumbnailColumn, "40px");
        resultsTable.addColumn(sensorColumn, "Sensor");
        resultsTable.setColumnWidth(sensorColumn, "100px");
        resultsTable.addColumn(dateColumn, "Acq date");
        resultsTable.setColumnWidth(dateColumn, "100px");
        tableBuilder.addDeployableColumn(StyleResources.INSTANCE.info(), StyleResources.INSTANCE.info());

        // enable quote button
        enableQuotingMayBe();

    }

    @Override
    public void displayImageProducts(List<Product> imageProductDTOs) {
        resultsPanel.getElement().getStyle().setProperty("height", (Window.getClientHeight() - 135 - queryPanel.getAbsoluteTop()) + "px");
        imagesList.getList().clear();
        imagesList.getList().addAll(imageProductDTOs == null ? new ArrayList<Product>() : imageProductDTOs);
        imagesList.refresh();
        // refresh map
        refreshMap();
    }

    private void refreshMap() {
        ArcgisMapJSNI arcgisMap = mapContainer.getArcgisMap();
        MapJSNI map = mapContainer.map;
        // refresh products display on map
        for(Product product : imagesList.getList()) {
            boolean toRender = selectedProducts.contains(product);
            boolean rendered = renderedProducts.containsKey(product);
            if(toRender && !rendered) {
                ProductRendering productRendering = new ProductRendering();
                PolygonJSNI polygon = arcgisMap.createPolygon(product.getCoordinatesWKT().replace("POLYGON((", "").replace("))", ""));
                productRendering.footprint = map.getGraphics().addGraphic(polygon,
                        arcgisMap.createFillSymbol(product.getType() == Product.TYPE.ARCHIVE ? "#ff0000" : "#00ffff", 2, "rgba(0,0,0,0.0)"));
                // add wms layer
                if (product.getType() == Product.TYPE.ARCHIVE && product.getQl() != null) {
                    productRendering.overlay = map.addWMSLayer(product.getQl(), WMSLayerInfoJSNI.createInfo(product.getSatelliteName(), product.getSatelliteName()), polygon.getExtent());
                }
                renderedProducts.put(product, productRendering);
            } else if(!toRender && rendered) {
                ProductRendering productRendering = renderedProducts.get(product);
                if(productRendering.footprint != null) {
                    map.getGraphics().remove(productRendering.footprint);
                }
                if(productRendering.overlay != null) {
                    map.removeWMSLayer(productRendering.overlay);
                }
                renderedProducts.remove(product);
            }
        }
        // remove previous outline product
        if(outlinedProductGraphicJSNI != null) {
            map.getGraphics().remove(outlinedProductGraphicJSNI);
        }
        // add outlined product on top
        if(outlinedProduct != null) {
            outlinedProductGraphicJSNI = map.getGraphics().addGraphic(arcgisMap.createPolygon(outlinedProduct.getCoordinatesWKT().replace("POLYGON((", "").replace("))", "")),
                    arcgisMap.createFillSymbol("#0000ff", 2, "rgba(0,0,0,0.2)"));
        }
    }

    private void outlineProduct(Product product) {
        outlinedProduct = product;
        refreshMap();
    }

    private void enableQuotingMayBe() {
        boolean enabled = true;
        // TODO - check products have been selected
        enableQuoting(enabled);
    }

    private void enableQuoting(boolean enabled) {
        submitForQuote.setEnabled(enabled);
    }

    @Override
    public void displayStartDate(Date date) {
        this.startDate.setValue(date);
    }

    @Override
    public void displayStopDate(Date date) {
        this.stopDate.setValue(date);
    }

    @Override
    public void displaySensors(String sensors) {
        this.sensors.setValue(sensors);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSuppliers(List<ImageService> imageServices) {
        providerDropdown.clear();
        for(final ImageService imageService : imageServices) {
            MaterialLink materialLink = new MaterialLink(imageService.getName());
            materialLink.setBackgroundColor("white");
            materialLink.setTextColor("black");
            materialLink.setTooltip(imageService.getDescription());
            materialLink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.onProviderChanged(imageService);
                }
            });
            providerDropdown.add(materialLink);
        }
    }

    @Override
    public void displayService(ImageService imageService) {
        providersLink.setText(imageService.getName());
    }

    @Override
    public HasClickHandlers getUpdateButton() {
        return update;
    }

    @Override
    public void enableUpdate(boolean enable) {
        update.setEnabled(enable);
    }

    @Override
    public void clearMap() {
        if(mapContainer != null) {
            mapContainer.clearAoI();
            clearProductsSelection();
        }
    }

    @Override
    public void displaySensorSuggestions(List<Suggestion> response) {
        sensors.displayListSearches(response);
    }

    @Override
    public HasClickHandlers getQuoteButton() {
        return submitForQuote;
    }

    @Override
    public List<Product> getSelectedProducts() {
        return new ArrayList<Product>(selectedProducts);
    }

    @Override
    public void clearProductsSelection() {
        MapJSNI map = mapContainer.map;
        if(renderedProducts.size() > 0) {
            for(ProductRendering productRendering : renderedProducts.values()) {
                map.getGraphics().remove(productRendering.footprint);
                map.removeWMSLayer(productRendering.overlay);
            }
            renderedProducts.clear();
            selectedProducts.clear();
        }
        if(outlinedProduct != null) {
            if(outlinedProductGraphicJSNI != null) {
                map.getGraphics().remove(outlinedProductGraphicJSNI);
            }
            outlinedProduct = null;
        }
        imagesList.refresh();
    }

    @Override
    public void displaySuccess(String message) {
        template.displaySuccess(message);
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void showQuery() {
        tab.selectTab("query");
    }

    @Override
    public void setSearchTextValid(boolean valid) {
        sensors.setSearchTextValid(valid);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void onResize(ResizeEvent event) {
        mapPanel.setHeight((Window.getClientHeight() - 64) + "px");
        template.setPanelStyleName(style.navOpened(), searchBar.isOpen());
    }

}