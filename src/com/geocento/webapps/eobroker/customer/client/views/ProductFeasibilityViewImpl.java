package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.styles.MyDataGridResources;
import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.forms.ElementEditor;
import com.geocento.webapps.eobroker.common.client.widgets.forms.FormHelper;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.*;
import com.geocento.webapps.eobroker.common.client.widgets.table.celltable.SubrowTableBuilder;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.styles.StyleResources;
import com.geocento.webapps.eobroker.customer.client.widgets.FeasibilityHeader;
import com.geocento.webapps.eobroker.customer.client.widgets.MaterialCheckBoxCell;
import com.geocento.webapps.eobroker.customer.client.widgets.PieOpt;
import com.geocento.webapps.eobroker.customer.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceFeasibilityDTO;
import com.geocento.webapps.eobroker.customer.shared.feasibility.CoverageFeature;
import com.geocento.webapps.eobroker.customer.shared.feasibility.DataSource;
import com.geocento.webapps.eobroker.customer.shared.feasibility.FEASIBILITY;
import com.geocento.webapps.eobroker.customer.shared.feasibility.ProductFeasibilityResponse;
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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import gwt.material.design.addins.client.cutout.MaterialCutOut;
import gwt.material.design.addins.client.sideprofile.MaterialSideProfile;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

import java.util.*;

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

        String navOpened();
    }

    @UiField
    Style style;

    @UiField
    MaterialDropDown serviceDropdown;
    @UiField
    MaterialLink servicesLink;
    @UiField
    MapContainer mapContainer;
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
    MaterialRow parameters;
    @UiField
    HTMLPanel mapPanel;
    @UiField
    HTMLPanel results;
    @UiField
    MaterialCutOut cutOut;
    @UiField
    HTMLPanel chartsArea;
    @UiField
    MaterialLink resultsTab;
    @UiField
    MaterialButton contact;
    @UiField
    MaterialButton request;
    @UiField
    HeaderPanel searchPanel;
    @UiField
    SimplePanel features;
    @UiField
    MaterialSideProfile sideProfile;
    @UiField
    MaterialLink feasibilityValue;
    @UiField
    MaterialLink coverageFeaturesValue;
    @UiField
    MaterialCollapsibleItem feasibilityPanel;
    @UiField
    LoadingWidget loadingResults;
    @UiField
    HTMLPanel statistics;
    @UiField
    MaterialLabel message;
    @UiField
    MaterialLabel feasibilityComment;
    @UiField
    MaterialChip supplier;
    @UiField
    MaterialImage serviceImage;

    private Presenter presenter;

    private DataGrid<CoverageFeature> resultsTable;

    private ListDataProvider<CoverageFeature> coverageFeaturesList;

    private HashSet<CoverageFeature> selectedFeatures = new HashSet<CoverageFeature>();

    private class FeatureRendering {
        GraphicJSNI geometry;
        WMSLayerJSNI overlay;
    }

    private HashMap<CoverageFeature, FeatureRendering> renderedFeatures = new HashMap<CoverageFeature, FeatureRendering>();

    private CoverageFeature outlinedRecord;

    private GraphicJSNI outlinedRecordGraphicJSNI;

    public ProductFeasibilityViewImpl(ClientFactoryImpl clientFactory) {

        initWidget(ourUiBinder.createAndBindUi(this));

        sideProfile.setBackgroundColor(CategoryUtils.getColor(Category.productservices));

        tab.setBackgroundColor(Color.TEAL_LIGHTEN_2);
        //resultsTab.setEnabled(false);

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

        createResultsTable();

        searchPanel.getElement().getParentElement().addClassName("z-depth-1");
        searchPanel.getElement().getParentElement().getStyle().setZIndex(10);

        mapContainer.setPresenter(aoi -> presenter.aoiChanged(aoi));

        Scheduler.get().scheduleDeferred(() -> {
            tab.selectTab("query");
            onResize(null);
        });
    }

    private void createResultsTable() {

        // create table
        resultsTable = new DataGrid<CoverageFeature>(20, MyDataGridResources.INSTANCE);
        resultsTable.setSize("100%", "100%");
        resultsTable.setPageSize(20);

        resultsTable.setEmptyTableWidget(new Label("No coverage features provided..."));

        // add a pager
        SimplePager pager = new SimplePager();
        pager.setDisplay(resultsTable);

        resultsTable.addCellPreviewHandler(new CellPreviewEvent.Handler<CoverageFeature>() {
            @Override
            public void onCellPreview(CellPreviewEvent<CoverageFeature> event) {
                if (BrowserEvents.MOUSEOVER.equals(event.getNativeEvent().getType())) {
                    outlineRecord(event.getValue());
                } else if (BrowserEvents.MOUSEOUT.equals(event.getNativeEvent().getType())) {
                    outlineRecord(null);
                }
            }
        });

        features.setWidget(resultsTable);

        SubrowTableBuilder tableBuilder = new SubrowTableBuilder<CoverageFeature>(resultsTable) {

            @Override
            protected String getInformation(CoverageFeature coverageFeature) {
                return coverageFeature.getDescription() == null ? "No information" : coverageFeature.getDescription();
            }

        };
        resultsTable.setTableBuilder(tableBuilder);
        displayResultMessage("No query performed, fill in your query in the query tab and press the check feasibility button");

        // define columns
        Column<CoverageFeature, Boolean> checkColumn = new Column<CoverageFeature, Boolean>(new MaterialCheckBoxCell()) {
            @Override
            public Boolean getValue(CoverageFeature record) {
                return selectedFeatures.contains(record);
            }
        };
        checkColumn.setFieldUpdater(new FieldUpdater<CoverageFeature, Boolean>() {
            @Override
            public void update(int index, CoverageFeature record, Boolean value) {
                if (selectedFeatures.contains(record) != value) {
                    if (value) {
                        selectedFeatures.add(record);
                    } else {
                        selectedFeatures.remove(record);
                    }
                    resultsTable.redraw();
                    refreshMap();
                }
            }
        });
        TextColumn<CoverageFeature> titleColumn = new TextColumn<CoverageFeature>() {
            @Override
            public String getValue(CoverageFeature object) {
                return object == null || object.getName() == null ? "Unknown" : object.getName();
            }
        };
        resultsTable.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        resultsTable.setColumnWidth(checkColumn, "30px");
        //resultsTable.addColumn(geometryColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        resultsTable.addColumn(titleColumn, "title");
        resultsTable.setColumnWidth(titleColumn, "100px");
        tableBuilder.addDeployableColumn(StyleResources.INSTANCE.info(), StyleResources.INSTANCE.info());

        coverageFeaturesList = new ListDataProvider<CoverageFeature>();
        coverageFeaturesList.addDataDisplay(resultsTable);

    }

    private void refreshMap() {
        ArcgisMapJSNI arcgisMap = mapContainer.getArcgisMap();
        MapJSNI map = mapContainer.map;
        // refresh products display on map
        for(CoverageFeature coverageFeature : resultsTable.getVisibleItems()) {
            // skip if no geometry
            if(coverageFeature.getGeometryWKT() == null) continue;
            boolean toRender = selectedFeatures.contains(coverageFeature);
            boolean rendered = renderedFeatures.containsKey(coverageFeature);
            if(toRender && !rendered) {
                FeatureRendering featureRendering = new FeatureRendering();
                String geometryWKT = coverageFeature.getGeometryWKT();
                GeometryJSNI geometryJSNI = arcgisMap.createGeometry(geometryWKT);
                featureRendering.geometry = map.getGraphics().addGraphic(geometryJSNI,
                        arcgisMap.createFillSymbol("#00ffff", 2, "rgba(0,0,0,0.0)"));
                renderedFeatures.put(coverageFeature, featureRendering);
            } else if(!toRender && rendered) {
                FeatureRendering featureRendering = renderedFeatures.get(coverageFeature);
                if(featureRendering.geometry != null) {
                    map.getGraphics().remove(featureRendering.geometry);
                }
                if(featureRendering.overlay != null) {
                    map.removeWMSLayer(featureRendering.overlay);
                }
                renderedFeatures.remove(coverageFeature);
            }
        }
        // remove previous outline record
        if(outlinedRecordGraphicJSNI != null) {
            map.getGraphics().remove(outlinedRecordGraphicJSNI);
        }
        // add outlined record on top
        if(outlinedRecord != null) {
            if(outlinedRecord.getGeometryWKT() != null) {
                GeometryJSNI geometryJSNI = arcgisMap.createGeometry(outlinedRecord.getGeometryWKT());
                outlinedRecordGraphicJSNI = map.getGraphics().addGraphic(geometryJSNI,
                        arcgisMap.createFillSymbol("#0000ff", 2, "rgba(0,0,0,0.2)"));
            }
        }
    }

    private void outlineRecord(CoverageFeature coverageFeature) {
        outlinedRecord = coverageFeature;
        refreshMap();
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
    public void centerOnAoI() {
        mapContainer.centerOnAoI();
    }

    @Override
    public void setText(String text) {

    }

    @Override
    public void displayLoadingResults(String message) {
        tab.selectTab("results");
        hideResults();
        loadingResults.setVisible(true);
        loadingResults.setText(message);
        onResize(null);
    }

    @Override
    public void hideLoadingResults() {
        hideResults();
        results.setVisible(true);
        onResize(null);
    }

    private void displayResultMessage(String message) {
        hideResults();
        this.message.setVisible(true);
        this.message.setText(message);
    }

    private void hideResults() {
        this.message.setVisible(false);
        loadingResults.setVisible(false);
        results.setVisible(false);
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
        mapContainer.map.getGraphics().clear();
    }

    @Override
    public void setServices(List<ProductServiceFeasibilityDTO> productServices) {
/*
        serviceDropdown.clear();
        for(final ProductServiceFeasibilityDTO productServiceFeasibilityDTO : productServices) {
            MaterialLink materialLink = new MaterialLink(productServiceFeasibilityDTO.getName());
            materialLink.setBackgroundColor(Color.WHITE);
            materialLink.setTextColor(Color.BLACK);
            materialLink.setTooltip("Service provided by " + productServiceFeasibilityDTO.getCompanyDTO().getName());
            materialLink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.onServiceChanged(productServiceFeasibilityDTO);
                }
            });
            serviceDropdown.add(materialLink);
        }
        // TODO - add somewhere else
        tab.selectTab("query");
        onResize(null);
*/
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
    public void selectService(ProductServiceFeasibilityDTO productServiceFeasibilityDTO) {
        servicesLink.setText(productServiceFeasibilityDTO.getName());
        serviceImage.setUrl(productServiceFeasibilityDTO.getImageURL());
        supplier.setText(productServiceFeasibilityDTO.getCompanyDTO().getName());
        supplier.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(
                new FullViewPlace(
                        Utils.generateTokens(FullViewPlace.TOKENS.companyid.toString(),
                                productServiceFeasibilityDTO.getCompanyDTO().getId() + ""))));
        tab.selectTab("query");
        onResize(null);
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
        results.setVisible(false);
        this.message.setVisible(true);
        this.message.setText(message);
    }

    @Override
    public void displayResponse(final ProductFeasibilityResponse response) {
        Color color = Color.RED;
        String text = "Unknown";
        if (response.getFeasible() == FEASIBILITY.NONE) {
            text = "NOT FEASIBLE";
            color = Color.RED;
        } else if (response.getFeasible() == FEASIBILITY.PARTIAL) {
            text = "PARTIAL";
            color = Color.ORANGE;
        } else if (response.getFeasible() == FEASIBILITY.GOOD) {
            text = "LIKELY";
            color = Color.AMBER;
        }
        feasibilityValue.setText(text);
        feasibilityValue.setTextColor(color);
        feasibilityComment.setText(response.getMessage());

        // set the coverage features values
        coverageFeaturesList.setList(response.getCoverages() == null ? new ArrayList<CoverageFeature>() : response.getCoverages());
        coverageFeaturesValue.setText(response.getCoverages() == null ? "" : response.getCoverages().size() + "");
        refreshMap();

        feasibilityPanel.expand();

        resultsTab.setEnabled(true);

/*
        resultsTab.setVisible(true);
        results.clear();
        // add main collapsible panel
        MaterialCollapsible materialCollapsible = new MaterialCollapsible();
        materialCollapsible.setBackgroundColor(Color.WHITE);
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
                feasibilityHeader.setIndicatorColor(Color.RED);
                return;
            } else if (response.getFeasible() == ProductFeasibilityResponse.FEASIBILITY.PARTIAL) {
                feasibilityHeader.setIndicatorText("PARTIAL");
                feasibilityHeader.setIndicatorColor(Color.ORANGE);
            } else if (response.getFeasible() == ProductFeasibilityResponse.FEASIBILITY.GOOD) {
                feasibilityHeader.setIndicatorText("LIKELY");
                feasibilityHeader.setIndicatorColor(Color.AMBER);
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
            imageCoverage.setHeaderText("AoIDTO coverage");
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
            // add coverage
            MapJSNI map = mapContainer.map;
            ArcgisMapJSNI arcgisMap = mapContainer.getArcgisMap();
            if(coverageGraphics != null) {
                map.getGraphics().remove(coverageGraphics);
            }
            coverageGraphics = map.getGraphics().addGraphic(arcgisMap.createGeometry(response.getCoverages().get(0).getGeometryWKT()),
                    arcgisMap.createFillSymbol("#ffff00", 2, "rgba(0,255,0,0.5)"));
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
*/
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

    private void displaySensors(Widget target, final List<DataSource> dataSources) {
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
                HashMap<DataSource.IMAGETYPE, Integer> sensorType = new HashMap<DataSource.IMAGETYPE, Integer>();
                for(DataSource dataSource : dataSources) {
                    DataSource.IMAGETYPE imageType = dataSource.getImagetype();
                    sensorType.put(imageType, sensorType.get(imageType) == null ? new Integer(1) : (sensorType.get(imageType) + 1));
                }
                dataTable.addRows(sensorType.size());
                int index = 0;
                for(DataSource.IMAGETYPE imageType : sensorType.keySet()) {
                    dataTable.setValue(index, 0, imageType.toString());
                    dataTable.setValue(index, 1, sensorType.get(imageType));
                }
                PieOpt opt = new PieOpt();
                opt.setColors("2196f3", "42a5f5", "64b5f6", "90caf9", "bbdefb");

                chart.draw(dataTable, opt.get());
            }
        });
        cutOut.open();
    }

    @UiHandler("btnCutOutClose")
    void closeCutOut(ClickEvent clickEvent) {
        cutOut.close();
    }

    @Override
    public void clearResults() {

    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void showQuery() {
        tab.selectTab("query");
    }

    @Override
    public HasClickHandlers getRequestButton() {
        return request;
    }

    @Override
    public HasClickHandlers getContactButton() {
        return contact;
    }

    @Override
    public void onResize(ResizeEvent event) {
        if(mapContainer.map != null) {
            mapContainer.map.resize();
        }
        searchPanel.onResize();
    }

}