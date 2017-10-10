package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.styles.MyCellTableResources;
import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.WidgetUtil;
import com.geocento.webapps.eobroker.common.client.widgets.charts.ChartPanel;
import com.geocento.webapps.eobroker.common.client.widgets.forms.ElementEditor;
import com.geocento.webapps.eobroker.common.client.widgets.forms.FormHelper;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.*;
import com.geocento.webapps.eobroker.common.client.widgets.table.celltable.SubrowTableBuilder;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.feasibility.*;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.styles.StyleResources;
import com.geocento.webapps.eobroker.customer.client.widgets.MaterialCheckBoxCell;
import com.geocento.webapps.eobroker.customer.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceFeasibilityDTO;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import gwt.material.design.addins.client.cutout.MaterialCutOut;
import gwt.material.design.addins.client.sideprofile.MaterialSideProfile;
import gwt.material.design.client.constants.Color;
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
    MaterialPanel statistics;
    @UiField
    MaterialLabel message;
    @UiField
    MaterialLabel feasibilityComment;
    @UiField
    MaterialChip supplier;
    @UiField
    MaterialImage serviceImage;
    @UiField
    SimplePanel pagerPanel;
    @UiField
    MaterialLabel messageCandidates;
    @UiField
    MaterialCollapsibleItem statisticsPanel;
    @UiField
    ChartPanel chartPanel;
    @UiField
    MaterialCollapsibleItem coveragePanel;

    private Presenter presenter;

    private CellTable<ProductCandidate> resultsTable;

    private ListDataProvider<ProductCandidate> coverageFeaturesList;

    private ColumnSortEvent.ListHandler<ProductCandidate> sortDataHandler;

    private HashSet<ProductCandidate> selectedFeatures = new HashSet<ProductCandidate>();

    private class FeatureRendering {
        GraphicJSNI geometry;
        WMSLayerJSNI overlay;
    }

    private HashMap<ProductCandidate, FeatureRendering> renderedFeatures = new HashMap<ProductCandidate, FeatureRendering>();

    private HashMap<WMSStatistics, Boolean> layerStatistics = new HashMap<WMSStatistics, Boolean>();
    private HashMap<WMSStatistics, WMSLayerJSNI> layerStatisticsDisplay = new HashMap<WMSStatistics, WMSLayerJSNI>();

    private ProductCandidate outlinedRecord;

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
        resultsTable = new CellTable<ProductCandidate>(20, MyCellTableResources.INSTANCE);
        resultsTable.setSize("100%", "100%");
        resultsTable.setPageSize(20);

        resultsTable.setEmptyTableWidget(new Label("No coverage features provided..."));

        // add a pager
        SimplePager pager = new SimplePager();
        pager.setDisplay(resultsTable);

        coverageFeaturesList = new ListDataProvider<ProductCandidate>();
        coverageFeaturesList.setList(new ArrayList<ProductCandidate>());
        coverageFeaturesList.addDataDisplay(resultsTable);

        sortDataHandler = new ColumnSortEvent.ListHandler<>(coverageFeaturesList.getList());
        resultsTable.addColumnSortHandler(sortDataHandler);

        resultsTable.addCellPreviewHandler(new CellPreviewEvent.Handler<ProductCandidate>() {
            @Override
            public void onCellPreview(CellPreviewEvent<ProductCandidate> event) {
                if (BrowserEvents.MOUSEOVER.equals(event.getNativeEvent().getType())) {
                    outlineRecord(event.getValue());
                } else if (BrowserEvents.MOUSEOUT.equals(event.getNativeEvent().getType())) {
                    outlineRecord(null);
                }
            }
        });

        features.setWidget(resultsTable);
        pagerPanel.setWidget(pager);

        SubrowTableBuilder tableBuilder = new SubrowTableBuilder<ProductCandidate>(resultsTable) {

            @Override
            protected String getInformation(ProductCandidate coverageFeature) {
                return coverageFeature.getDescription() == null ? "No information" : coverageFeature.getDescription();
            }

        };
        resultsTable.setTableBuilder(tableBuilder);

/*
        displayResultMessage("No query performed, fill in your query in the query tab and press the check feasibility button");
*/

        // define columns
        Column<ProductCandidate, Boolean> checkColumn = new Column<ProductCandidate, Boolean>(new MaterialCheckBoxCell()) {
            @Override
            public Boolean getValue(ProductCandidate record) {
                return selectedFeatures.contains(record);
            }
        };
        checkColumn.setFieldUpdater(new FieldUpdater<ProductCandidate, Boolean>() {
            @Override
            public void update(int index, ProductCandidate record, Boolean value) {
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
        TextColumn<ProductCandidate> titleColumn = new TextColumn<ProductCandidate>() {
            @Override
            public String getValue(ProductCandidate object) {
                return object == null || object.getName() == null ? "Unknown" : object.getName();
            }
        };
        resultsTable.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        resultsTable.setColumnWidth(checkColumn, "30px");
        //resultsTable.addColumn(geometryColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        resultsTable.addColumn(titleColumn, "title");
        //resultsTable.setColumnWidth(titleColumn, "100px");
        tableBuilder.addDeployableColumn(StyleResources.INSTANCE.info(), StyleResources.INSTANCE.info());

    }

    private void refreshMap() {
        ArcgisMapJSNI arcgisMap = mapContainer.getArcgisMap();
        MapJSNI map = mapContainer.map;
        // refresh products display on map
        for(ProductCandidate productCandidate : resultsTable.getVisibleItems()) {
            // skip if no geometry
            if(productCandidate.getGeometryWKT() == null) continue;
            boolean toRender = selectedFeatures.contains(productCandidate);
            boolean rendered = renderedFeatures.containsKey(productCandidate);
            if(toRender && !rendered) {
                FeatureRendering featureRendering = new FeatureRendering();
                String geometryWKT = productCandidate.getGeometryWKT();
                GeometryJSNI geometryJSNI = arcgisMap.createGeometry(geometryWKT);
                featureRendering.geometry = map.getGraphics().addGraphic(geometryJSNI,
                        arcgisMap.createFillSymbol("#00ffff", 2, "rgba(0,0,0,0.0)"));
                renderedFeatures.put(productCandidate, featureRendering);
            } else if(!toRender && rendered) {
                FeatureRendering featureRendering = renderedFeatures.get(productCandidate);
                if(featureRendering.geometry != null) {
                    map.getGraphics().remove(featureRendering.geometry);
                }
                if(featureRendering.overlay != null) {
                    map.removeWMSLayer(featureRendering.overlay);
                }
                renderedFeatures.remove(productCandidate);
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
        // now refresh the map layers
        for(WMSStatistics wmsStatistics : layerStatistics.keySet()) {
            WMSLayerJSNI displayLayer = layerStatisticsDisplay.get(wmsStatistics);
            if(layerStatistics.get(wmsStatistics)) {
                if(displayLayer == null) {
                    layerStatisticsDisplay.put(wmsStatistics, mapContainer.map.addWMSLayer(wmsStatistics.getBaseUrl(), wmsStatistics.getLayerName()));
                }
            } else {
                if(displayLayer != null) {
                    mapContainer.map.removeWMSLayer(displayLayer);
                    layerStatisticsDisplay.remove(wmsStatistics);
                }
            }
        }
    }

    private void outlineRecord(ProductCandidate productCandidate) {
        outlinedRecord = productCandidate;
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
        servicesLink.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(Utils.generateTokens(
                FullViewPlace.TOKENS.productserviceid.toString(), productServiceFeasibilityDTO.getId() + ""
        ))));
        serviceImage.setUrl(productServiceFeasibilityDTO.getImageURL());
        supplier.setText(productServiceFeasibilityDTO.getCompany().getName());
        supplier.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(
                new FullViewPlace(
                        Utils.generateTokens(FullViewPlace.TOKENS.companyid.toString(),
                                productServiceFeasibilityDTO.getCompany().getId() + ""))));
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
    public void displayResponse(final FeasibilityResponse response) {
        selectedFeatures.clear();

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
        coverageFeaturesList.getList().clear();
        List<ProductCandidate> productCandidates = response.getProductCandidates();
        boolean hasProductCandidates = productCandidates != null && productCandidates.size() > 0;
        if(hasProductCandidates) {
            coverageFeaturesList.getList().addAll(productCandidates);
        }
        resultsTable.setVisible(hasProductCandidates);
        coverageFeaturesValue.setText(!hasProductCandidates ? "" : productCandidates.size() + "");
        messageCandidates.setText(hasProductCandidates ? "List of possible products" : "No product available");
        pagerPanel.setVisible(hasProductCandidates && response.getProductCandidates().size() > resultsTable.getPageSize());
        refreshMap();

        feasibilityPanel.expand();
        if(hasProductCandidates) {
            coveragePanel.expand();
            selectedFeatures.add(productCandidates.get(0));
        }

        resultsTab.setEnabled(true);

        // add stats
        chartPanel.clear();
        if(response.getStatistics() != null && response.getStatistics().size() > 0){
            chartPanel.loadChartAPI(new Runnable() {
                @Override
                public void run() {
                    // make sure the panel is expanded to get the right width
                    // check available stats
                    for (Statistics statistics : response.getStatistics()) {
                        // TODO - add to map instead?
                        if(statistics instanceof WMSStatistics) {
                            layerStatistics.put((WMSStatistics) statistics, false);
                            MaterialPanel materialPanel = new MaterialPanel();
                            MaterialCheckBox materialCheckBox = new MaterialCheckBox(statistics.getName());
                            materialCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                                @Override
                                public void onValueChange(ValueChangeEvent<Boolean> event) {
                                    layerStatistics.put((WMSStatistics) statistics, event.getValue());
                                    refreshMap();
                                }
                            });
                            materialPanel.add(materialCheckBox);
                            chartPanel.add(materialPanel);
                        } else {
                            chartPanel.addStatistics(statistics);
                        }
                    }
                    //chartPanel.setWidth();
                    chartPanel.onResize(null);
                }
            });
        } else {
            MaterialLabel materialLabel = new MaterialLabel("No statistic has been provided");
            materialLabel.addStyleName(style.subsection());
            chartPanel.add(materialLabel);
        }
    }

    @UiHandler("btnCutOutClose")
    void closeCutOut(ClickEvent clickEvent) {
        cutOut.close();
    }

    @Override
    public void clearResults() {
        selectedFeatures.clear();
        layerStatisticsDisplay.clear();
        refreshMap();
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
        chartPanel.onResize(null);
    }

}