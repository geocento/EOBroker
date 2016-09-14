package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.styles.MyDataGridResources;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.ArcGISMap;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.*;
import com.geocento.webapps.eobroker.common.client.widgets.table.celltable.SubrowTableBuilder;
import com.geocento.webapps.eobroker.common.shared.LatLng;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.ImageService;
import com.geocento.webapps.eobroker.common.shared.imageapi.Product;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.styles.StyleResources;
import com.geocento.webapps.eobroker.customer.client.widgets.MaterialCheckBoxCell;
import com.geocento.webapps.eobroker.customer.client.widgets.MaterialSensorsSuggestion;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
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
    ArcGISMap mapContainer;
    @UiField
    MaterialAnchorButton drawPolygon;
    @UiField
    MaterialAnchorButton clearAoIs;
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

    private Callback<Void, Exception> mapLoadedHandler = null;

    private MapJSNI map;

    private GraphicJSNI aoiRendering;

    private Product outlinedProduct;

    private GraphicJSNI outlinedProductGraphicJSNI;

    private boolean mapLoaded = false;

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

        mapContainer.loadArcGISMap(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {

            }

            @Override
            public void onSuccess(Void result) {
                mapContainer.createMap("streets", new LatLng(40.0, -4.0), 3, new com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback<MapJSNI>() {

                    @Override
                    public void callback(final MapJSNI mapJSNI) {
                        final ArcgisMapJSNI arcgisMap = mapContainer.arcgisMap;
                        ImageSearchViewImpl.this.map = mapJSNI;
                        final DrawJSNI drawJSNI = arcgisMap.createDraw(mapJSNI);
                        drawJSNI.onDrawEnd(new com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback<DrawEventJSNI>() {

                            @Override
                            public void callback(DrawEventJSNI result) {
                                drawJSNI.deactivate();
                                AoI aoi = AoIUtil.createAoI(arcgisMap.convertsToGeographic(result.getGeometry()));
                                displayAoI(aoi);
                                presenter.aoiChanged(aoi);
                            }
                        });
                        drawPolygon.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                mapJSNI.getGraphics().clear();
                                drawJSNI.activate("polygon");
                            }
                        });
                        clearAoIs.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                mapJSNI.getGraphics().clear();
                                presenter.aoiChanged(null);
                            }
                        });
                        mapLoaded();
                    }
                });
            }
        });
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

        onResize(null);
    }

    private void mapLoaded() {
        mapLoaded = true;
        if(mapLoadedHandler != null) {
            mapLoadedHandler.onSuccess(null);
        }
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        this.mapLoadedHandler = mapLoadedHandler;
        if(mapLoaded) {
            mapLoadedHandler.onSuccess(null);
        }
    }

    @Override
    public void displayAoI(AoI aoi) {
        if(aoiRendering != null) {
            map.getGraphics().remove(aoiRendering);
        }
        if(aoi != null) {
            aoiRendering = map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometryFromAoI(aoi), mapContainer.arcgisMap.createFillSymbol("#ff00ff", 2, "rgba(0,0,0,0.2)"));
        }
    }

    @Override
    public void setText(String text) {
        sensors.setText(text);
    }

    @Override
    public void displayLoadingResults(String message) {
        template.setLoading(message);
        searchBar.hide();
    }

    @Override
    public void hideLoadingResults() {
        template.hideLoading();
        searchBar.show();
        tab.selectTab("results");
    }

    private void createResultsTable() {
        // create table
        resultsTable = new DataGrid<Product>(1000, MyDataGridResources.INSTANCE);
        resultsTable.setSize("100%", "100%");
        //resultsTable.setStyleName("striped");
/*
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(resultsTable);
*/
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

/*
        Column<Product, Boolean> actionColumn = new Column<Product, Boolean>(new ClickableImageCell<Boolean>() {
            @Override
            protected void performAction(String action, Boolean value, NativeEvent event) {

            }

            @Override
            public void render(Context context, Boolean value, SafeHtmlBuilder sb) {
                sb.append(templates.cell("Show information on this product", "Show information on this product", imgStyle, makeImage(StyleResources.INSTANCE.info())));
            }
        }) {
            @Override
            public Boolean getValue(Product object) {
                return object != null;
            }
        };
*/

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
/*
        resultsTable.setRowCount(imageProductDTOs.size());
        resultsTable.setRowData(0, imageProductDTOs);
*/
        // refresh map
        refreshMap();
    }

    private void refreshMap() {
        // refresh products display on map
        for(Product product : imagesList.getList()) {
            boolean toRender = selectedProducts.contains(product);
            boolean rendered = renderedProducts.containsKey(product);
            if(toRender && !rendered) {
                ProductRendering productRendering = new ProductRendering();
                PolygonJSNI polygon = mapContainer.arcgisMap.createPolygon(product.getCoordinatesWKT().replace("POLYGON((", "").replace("))", ""));
                productRendering.footprint = map.getGraphics().addGraphic(polygon,
                        mapContainer.arcgisMap.createFillSymbol(product.getType() == Product.TYPE.ARCHIVE ? "#ff0000" : "#00ffff", 2, "rgba(0,0,0,0.0)"));
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
            outlinedProductGraphicJSNI = map.getGraphics().addGraphic(mapContainer.arcgisMap.createPolygon(outlinedProduct.getCoordinatesWKT().replace("POLYGON((", "").replace("))", "")),
                    mapContainer.arcgisMap.createFillSymbol("#0000ff", 2, "rgba(0,0,0,0.2)"));
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
        if(map != null) {
            if(map.getGraphics() != null) {
                if(aoiRendering != null) {
                    map.getGraphics().remove(aoiRendering);
                }
                clearProductsSelection();
            }
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
    public Widget asWidget() {
        return this;
    }

    @Override
    public void onResize(ResizeEvent event) {
        mapPanel.setHeight((Window.getClientHeight() - 64) + "px");
        template.setPanelStyleName(style.navOpened(), searchBar.isVisible());
    }

}