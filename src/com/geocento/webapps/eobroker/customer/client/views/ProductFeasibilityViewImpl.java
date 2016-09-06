package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.forms.ElementEditor;
import com.geocento.webapps.eobroker.common.client.widgets.forms.FormHelper;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.ArcGISMap;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.*;
import com.geocento.webapps.eobroker.common.shared.LatLng;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.imageapi.Product;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.widgets.FeasibilityHeader;
import com.geocento.webapps.eobroker.customer.client.widgets.PieOpt;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceFeasibilityDTO;
import com.geocento.webapps.eobroker.customer.shared.feasibility.Feature;
import com.geocento.webapps.eobroker.customer.shared.feasibility.ProductFeasibilityResponse;
import com.geocento.webapps.eobroker.customer.shared.feasibility.Sensor;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import gwt.material.design.addins.client.cutout.MaterialCutOut;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductFeasibilityViewImpl extends Composite implements ProductFeasibilityView, ResizeHandler {

    interface ProductFeasibilityViewUiBinder extends UiBinder<Widget, ProductFeasibilityViewImpl> {
    }

    private static ProductFeasibilityViewUiBinder ourUiBinder = GWT.create(ProductFeasibilityViewUiBinder.class);

    public static interface Style extends CssResource {
        String section();

        String subsection();
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialDropDown serviceDropdown;
    @UiField
    MaterialLink servicesLink;
    @UiField
    ArcGISMap mapContainer;
    @UiField
    MaterialAnchorButton drawPolygon;
    @UiField
    MaterialAnchorButton clearAoIs;
    @UiField
    MaterialTab tab;
    @UiField
    MaterialDatePicker startDate;
    @UiField
    MaterialDatePicker stopDate;
    @UiField
    MaterialRow queryPanel;
    @UiField
    MaterialButton update;
    @UiField
    MaterialSideNav searchBar;
    @UiField
    MaterialRow parameters;
    @UiField
    HTMLPanel mapPanel;
    @UiField
    HTMLPanel results;
    @UiField
    MaterialCutOut cutOut;
    @UiField
    Anchor sensors;
    @UiField
    HTMLPanel chartsArea;
    @UiField
    MaterialLink resultsTab;

    private Presenter presenter;

    private Callback<Void, Exception> mapLoadedHandler = null;

    private MapJSNI map;

    private boolean mapLoaded = false;

    private CellTable<Product> resultsTable;

    private final ProvidesKey<Product> KEY_PROVIDER = new ProvidesKey<Product>() {
        @Override
        public Object getKey(Product item) {
            return item.getProductId();
        }
    };
    private final SelectionModel<Product> selectionModel = new MultiSelectionModel<Product>(KEY_PROVIDER);

    private GraphicJSNI coverageGraphics = null;

    public ProductFeasibilityViewImpl(ClientFactoryImpl clientFactory) {
        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));
        onResize(null);
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
                        ProductFeasibilityViewImpl.this.map = mapJSNI;
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
        resultsTab.setVisible(false);

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
        map.getGraphics().clear();
        if(aoi != null) {
            map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometryFromAoI(aoi), mapContainer.arcgisMap.createFillSymbol("#ff00ff", 2, "rgba(0,0,0,0.2)"));
        }
    }

    @Override
    public void setText(String text) {

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

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
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
        map.getGraphics().clear();
    }

    @Override
    public void displayLoading(String message) {
        template.setLoading(message);
    }

    @Override
    public void setServices(List<ProductServiceFeasibilityDTO> productServices) {
        serviceDropdown.clear();
        for(final ProductServiceFeasibilityDTO productServiceFeasibilityDTO : productServices) {
            MaterialLink materialLink = new MaterialLink(ButtonType.RAISED, productServiceFeasibilityDTO.getName(), new MaterialIcon(IconType.ADD_A_PHOTO));
            materialLink.setBackgroundColor("white");
            materialLink.setTextColor("black");
            materialLink.setTooltip("Service provided by " + productServiceFeasibilityDTO.getCompanyName());
            materialLink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.onServiceChanged(productServiceFeasibilityDTO);
                }
            });
            serviceDropdown.add(materialLink);
        }
    }

    @Override
    public void setFormElements(List<FormElement> apiFormElements) {
        // remove previous elements
        parameters.clear();
        for(FormElement formElement : apiFormElements) {
            MaterialColumn materialColumn = new MaterialColumn(12, 12, 6);
            ElementEditor editor = FormHelper.createEditor(formElement);
            materialColumn.add(editor);
            parameters.add(materialColumn);
        }
    }

    @Override
    public void hideLoading() {
        template.hideLoading();
    }

    @Override
    public void displayError(String message) {
        template.displayError(message);
    }

    @Override
    public void selectService(ProductServiceFeasibilityDTO productServiceFeasibilityDTO) {
        servicesLink.setText(productServiceFeasibilityDTO.getName());
    }

    @Override
    public void setStart(Date start) {
        startDate.setDate(start);
    }

    @Override
    public void setStop(Date stop) {
        stopDate.setDate(stop);
    }

    @Override
    public void setFormElementValues(List<FormElementValue> formElementValues) {
        for(int index = 0; index < parameters.getWidgetCount(); index++) {
            MaterialColumn materialColumn = (MaterialColumn) parameters.getWidget(index);
            if(materialColumn.getWidget(0) instanceof ElementEditor) {
                ElementEditor elementEditor = (ElementEditor) materialColumn.getWidget(0);
                // look for matching form element value
                for(FormElementValue formElementValue : formElementValues) {
                    if(formElementValue.getFormid().contentEquals(elementEditor.getFormElement().getFormid())) {
                        elementEditor.setFormElementValue(formElementValue.getValue());
                    }
                }
            }
        }
    }

    @Override
    public void displayResultsError(String message) {
        results.add(new MaterialLabel(message));
    }

    @Override
    public void displayResponse(final ProductFeasibilityResponse response) {
        resultsTab.setVisible(true);
        results.clear();
        // add main collapsible panel
        MaterialCollapsible materialCollapsible = new MaterialCollapsible();
        materialCollapsible.setBackgroundColor("white");
        results.add(materialCollapsible);

        // add feasibility result
        {
            MaterialCollapsibleItem materialCollapsibleItem = new MaterialCollapsibleItem();
            materialCollapsible.add(materialCollapsibleItem);
            // add the header
            FeasibilityHeader feasibilityHeader = addFeasibilityHeader(materialCollapsibleItem, "Feasibility", IconType.TRAFFIC);
            // add the message
            HTMLPanel messagePanel = new HTMLPanel("<p>" + response.getMessage() + "</p>");
            messagePanel.addStyleName(style.section());
            MaterialCollapsibleBody materialCollapsibleBody = new MaterialCollapsibleBody(messagePanel);
            materialCollapsibleItem.add(materialCollapsibleBody);
            if (response.getFeasible() == ProductFeasibilityResponse.FEASIBILITY.NONE) {
                feasibilityHeader.setIndicatorText("NOT FEASIBLE");
                feasibilityHeader.setIndicatorColor("red");
                return;
            } else if (response.getFeasible() == ProductFeasibilityResponse.FEASIBILITY.PARTIAL) {
                feasibilityHeader.setIndicatorText("PARTIAL");
                feasibilityHeader.setIndicatorColor("orange");
            } else if (response.getFeasible() == ProductFeasibilityResponse.FEASIBILITY.GOOD) {
                feasibilityHeader.setIndicatorText("LIKELY");
                feasibilityHeader.setIndicatorColor("green");
            }
        }

        // add the coverage section
        {
            MaterialCollapsibleItem materialCollapsibleItem = new MaterialCollapsibleItem();
            materialCollapsible.add(materialCollapsibleItem);
            // add the header
            FeasibilityHeader feasibilityHeader = addFeasibilityHeader(materialCollapsibleItem, "Potential coverage", IconType.LAYERS);
            feasibilityHeader.setIndicatorText(NumberFormat.getFormat(".#").format(response.getBestCoverageValue() * 100.0) + "%");
            // add the different values
            HTMLPanel valuesPanel = new HTMLPanel("");
            valuesPanel.addStyleName(style.section());
            MaterialCollapsibleBody materialCollapsibleBody = new MaterialCollapsibleBody(valuesPanel);
            materialCollapsibleItem.add(materialCollapsibleBody);
            final FeasibilityHeader sensorsUsed = new FeasibilityHeader();
            sensorsUsed.setHeaderText("Sensors used");
            sensorsUsed.setIndicatorText(response.getSensors().size() + "");
            sensorsUsed.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    displaySensors(sensorsUsed, response.getSensors());
                }
            });
            valuesPanel.add(sensorsUsed);
            final FeasibilityHeader imageCoverage = new FeasibilityHeader();
            imageCoverage.setHeaderText("AoI coverage");
            imageCoverage.setIndicatorText(NumberFormat.getFormat(".#").format(response.getBestCoverageValue() * 100.0) + "%");
            imageCoverage.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    displaySensors(imageCoverage, response.getSensors());
                }
            });
            valuesPanel.add(imageCoverage);
            final FeasibilityHeader revisitRate = new FeasibilityHeader();
            revisitRate.setHeaderText("Revisit rate");
            revisitRate.setIndicatorText(response.getBestRevisitRate() == null ? "none" : response.getBestRevisitRate() + " days");
            revisitRate.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    displaySensors(revisitRate, response.getSensors());
                }
            });
            valuesPanel.add(revisitRate);
            if(coverageGraphics != null) {
                map.getGraphics().remove(coverageGraphics);
            }
            coverageGraphics = map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometry(response.getCoverages().get(0).getWktValue()), mapContainer.arcgisMap.createFillSymbol("#ffff00", 2, "rgba(0,255,0,0.5)"));
/*
            map.getGraphics().addGraphic(mapContainer.arcgisMap.createPolygon("-16.81223216085422 49.05194290024209, -16.5790489494 49.0826497873, -16.5423974816 48.9586665005, -16.4235627988 48.5540737686, -16.3058519277 48.1493942312, -16.1892300464 47.7446292693, -16.0736629334 47.3397802221, -16.6984677846 47.2553208808, -16.71110900596498 47.297803281945534, -17.0006058124 47.2656723917, -17.020966988398822 47.34970314429434, -17.0266160406 47.3260191601, -17.5207610899 47.3800108112, -17.5011721643 47.4655540906, -17.4081102472 47.8704668722, -17.3144860075 48.2753232278, -17.281477719774465 48.41715789630319, -17.2970527679 48.4805363036, -17.3972709915 48.8853667779, -17.4175257765 48.9668194183, -17.222494284395058 48.98766414768615, -17.2255361119 48.9975166932, -17.14339717383268 49.00833321416032, -17.1254698632 49.0848647921, -16.81223216085422 49.05194290024209"), mapContainer.arcgisMap.createFillSymbol("#ffff00", 2, "rgba(0,255,0,0.5)"));
*/
        }
        {
            MaterialCollapsibleItem materialCollapsibleItem = new MaterialCollapsibleItem();
            materialCollapsible.add(materialCollapsibleItem);
            // add the header
            FeasibilityHeader feasibilityHeader = addFeasibilityHeader(materialCollapsibleItem, "Delivery", IconType.LAYERS);
            feasibilityHeader.setIndicatorText("");
            // add the different values
            HTMLPanel valuesPanel = new HTMLPanel("");
            valuesPanel.addStyleName(style.section());
            MaterialCollapsibleBody materialCollapsibleBody = new MaterialCollapsibleBody(valuesPanel);
            materialCollapsibleItem.add(materialCollapsibleBody);
            // add information on time to delivery
            MaterialLabel timeToDelivery = new MaterialLabel("Time to delivery");
            final MaterialLink timeView = new MaterialLink("Not provided");
            timeView.setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
            timeToDelivery.add(timeView);
            timeView.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                }
            });
            // add information on data delivered formats
            MaterialLabel formatsDelivered = new MaterialLabel("Formats: PDF, Shapefile, OGC");
            valuesPanel.add(formatsDelivered);
        }
        {
            MaterialCollapsibleItem materialCollapsibleItem = new MaterialCollapsibleItem();
            materialCollapsible.add(materialCollapsibleItem);
            // add the header
            FeasibilityHeader feasibilityHeader = addFeasibilityHeader(materialCollapsibleItem, "Output", IconType.DATA_USAGE);
            feasibilityHeader.setIndicatorText("");
            // add the different values
            HTMLPanel valuesPanel = new HTMLPanel("");
            valuesPanel.addStyleName(style.section());
            MaterialCollapsibleBody materialCollapsibleBody = new MaterialCollapsibleBody(valuesPanel);
            materialCollapsibleItem.add(materialCollapsibleBody);
            MaterialLabel features = new MaterialLabel("Features");
            features.addStyleName(style.subsection());
            valuesPanel.add(features);
            for(Feature feature : response.getFeatures()) {
                valuesPanel.add(new MaterialTooltip(new MaterialLabel(feature.getName()), feature.getDescription()));
            }
            MaterialLabel sampleOutput = new MaterialLabel("Sample output");
            sampleOutput.addStyleName(style.subsection());
            valuesPanel.add(sampleOutput);
            if(response.getSamples() != null) {
                MaterialLink pdfReport = new MaterialLink();
                pdfReport.setText("Sample report");
                pdfReport.setIconType(IconType.FILE_DOWNLOAD);
                pdfReport.setHref(response.getSamples().getPdfReport());
                pdfReport.setTarget("_blank;");
                pdfReport.setDisplay(Display.BLOCK);
                pdfReport.setHeight("2em");
                valuesPanel.add(pdfReport);
                MaterialLink wmsLink = new MaterialLink();
                wmsLink.setText("OGC service");
                wmsLink.setIconType(IconType.MAP);
                wmsLink.setHref(response.getSamples().getWmsLayer());
                wmsLink.setTarget("_blank;");
                wmsLink.setDisplay(Display.BLOCK);
                wmsLink.setHeight("2em");
                valuesPanel.add(wmsLink);
            }
        }
        {
            MaterialCollapsibleItem materialCollapsibleItem = new MaterialCollapsibleItem();
            materialCollapsible.add(materialCollapsibleItem);
            // add the header
            FeasibilityHeader feasibilityHeader = addFeasibilityHeader(materialCollapsibleItem, "Additional information", IconType.LAYERS);
            feasibilityHeader.setIndicatorText("");
            // add the different values
            HTMLPanel valuesPanel = new HTMLPanel("Additional text TBD provided by supplier");
            valuesPanel.addStyleName(style.section());
            MaterialCollapsibleBody materialCollapsibleBody = new MaterialCollapsibleBody(valuesPanel);
            materialCollapsibleItem.add(materialCollapsibleBody);
        }
    }

    private FeasibilityHeader addFeasibilityHeader(MaterialCollapsibleItem materialCollapsibleItem, String text, IconType iconType) {
        MaterialCollapsibleHeader materialCollapsibleHeader = new MaterialCollapsibleHeader();
        materialCollapsibleItem.add(materialCollapsibleHeader);
        FeasibilityHeader feasibilityHeader = new FeasibilityHeader();
        feasibilityHeader.setHeaderText(text);
        feasibilityHeader.setHeaderIcon(iconType);
        materialCollapsibleHeader.add(feasibilityHeader);
        return feasibilityHeader;
    }

    private void displaySensors(Widget target, final List<Sensor> sensors) {
        cutOut.setTarget(target);
        chartsArea.clear();
        MaterialRow materialRow = new MaterialRow();
        chartsArea.add(materialRow);
        final MaterialColumn materialColumn = new MaterialColumn(12, 6, 4);
        materialRow.add(materialColumn);
        MaterialCard materialCard = new MaterialCard();
        MaterialCardTitle materialCardTitle = new MaterialCardTitle();
        materialCardTitle.setText("Potential sensors used for service");
        materialCard.add(materialCardTitle);
        final MaterialCardContent materialCardContent = new MaterialCardContent();
        materialCard.add(materialCardContent);
        materialColumn.add(materialCard);
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
        chartLoader.loadApi(new Runnable() {

            @Override
            public void run() {
                PieChart chart = new PieChart();
                chart.setWidth("100%");
                chart.setHeight("100%");
                materialCardContent.add(chart);
                DataTable dataTable = DataTable.create();
                dataTable.addColumn(ColumnType.STRING, "Sensor type");
                dataTable.addColumn(ColumnType.NUMBER, "Number of sensors");
                HashMap<Sensor.IMAGETYPE, Integer> sensorType = new HashMap<Sensor.IMAGETYPE, Integer>();
                for(Sensor sensor : sensors) {
                    Sensor.IMAGETYPE imageType = sensor.getImagetype();
                    sensorType.put(imageType, sensorType.get(imageType) == null ? new Integer(1) : (sensorType.get(imageType) + 1));
                }
                dataTable.addRows(sensorType.size());
                int index = 0;
                for(Sensor.IMAGETYPE imageType : sensorType.keySet()) {
                    dataTable.setValue(index, 0, imageType.toString());
                    dataTable.setValue(index, 1, sensorType.get(imageType));
                }
                PieOpt opt = new PieOpt();
                opt.setColors("2196f3", "42a5f5", "64b5f6", "90caf9", "bbdefb");

                chart.draw(dataTable, opt.get());
            }
        });
        cutOut.openCutOut();
    }

    @UiHandler("btnCutOutClose")
    void closeCutOut(ClickEvent clickEvent) {
        cutOut.closeCutOut();
    }

    @Override
    public void clearResults() {
        results.clear();
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
    }

}