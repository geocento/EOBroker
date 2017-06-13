package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.styles.MyDataGridResources;
import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.utils.opensearch.Record;
import com.geocento.webapps.eobroker.common.client.utils.opensearch.SearchResponse;
import com.geocento.webapps.eobroker.common.client.widgets.ImageCell;
import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLabelIcon;
import com.geocento.webapps.eobroker.common.client.widgets.forms.ElementEditor;
import com.geocento.webapps.eobroker.common.client.widgets.forms.FormHelper;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.*;
import com.geocento.webapps.eobroker.common.client.widgets.table.celltable.SubrowTableBuilder;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.styles.StyleResources;
import com.geocento.webapps.eobroker.customer.client.widgets.MaterialCheckBoxCell;
import com.geocento.webapps.eobroker.customer.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.customer.shared.ProductDatasetCatalogueDTO;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.HasData;
import gwt.material.design.addins.client.sideprofile.MaterialSideProfile;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

import java.util.*;

/**
 * Created by thomas on 09/05/2016.
 */
public class CatalogueSearchViewImpl extends Composite implements CatalogueSearchView, ResizeHandler {

    interface SearchPageUiBinder extends UiBinder<Widget, CatalogueSearchViewImpl> {
    }

    private static SearchPageUiBinder ourUiBinder = GWT.create(SearchPageUiBinder.class);

    static public interface Style extends CssResource {
        String navOpened();

        String editor();
    }

    @UiField
    Style style;

    @UiField
    MapContainer mapContainer;
    @UiField
    MaterialTab tab;
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
    HTMLPanel mapPanel;
    @UiField
    MaterialButton submitForQuote;
    @UiField
    HeaderPanel searchPanel;
    @UiField
    MaterialTextBox query;
    @UiField
    MaterialLabelIcon protocol;
    @UiField
    MaterialRow additionalFields;
    @UiField
    MaterialLabel name;
    @UiField
    MaterialChip supplier;
    @UiField
    MaterialImage image;
    @UiField
    MaterialLink additionalFieldsLabel;
    @UiField
    DisclosurePanel additionalFieldsPanel;
    @UiField
    MaterialLabelIcon resultsMessage;
    @UiField
    DockLayoutPanel panel;
    @UiField
    LoadingWidget loadingResults;
    @UiField
    MaterialPanel resultsContainer;
    @UiField
    SimplePanel pagerPanel;
    @UiField
    MaterialSideProfile sideProfile;

    private Presenter presenter;

    private Record outlinedRecord;

    private GraphicJSNI outlinedRecordGraphicJSNI;

    private DataGrid<Record> resultsTable;

    private AsyncDataProvider<Record> recordsList;

    private ColumnSortEvent.ListHandler<Record> sortDataHandler;

    private HashSet<Record> selectedRecord = new HashSet<Record>();

    private class RecordRendering {
        GraphicJSNI geometry;
        WMSLayerJSNI overlay;
    }

    private HashMap<Record, RecordRendering> renderedRecords = new HashMap<Record, RecordRendering>();

    public CatalogueSearchViewImpl(ClientFactoryImpl clientFactory) {

        initWidget(ourUiBinder.createAndBindUi(this));

        tab.setBackgroundColor(Color.TEAL_LIGHTEN_2);

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
        query.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.onQueryChanged(event.getValue());
            }
        });
        createResultsTable();

        searchPanel.getElement().getParentElement().addClassName("z-depth-1");
        searchPanel.getElement().getParentElement().getStyle().setZIndex(10);

        sideProfile.setBackgroundColor(CategoryUtils.getColor(Category.productdatasets));

        mapContainer.setPresenter(aoi -> presenter.aoiChanged(aoi));

        additionalFieldsPanel.setOpen(false);
        additionalFieldsPanel.addOpenHandler(event -> updateAdditionalFieldsMessage());
        additionalFieldsPanel.addCloseHandler(event -> updateAdditionalFieldsMessage());
        updateAdditionalFieldsMessage();

        Window.addResizeHandler(this);
    }

    private void updateAdditionalFieldsMessage() {
        boolean isOpen = additionalFieldsPanel.isOpen();
        additionalFieldsLabel.setText(isOpen ? "Hide additional fields" : "See additional fields");
        additionalFieldsLabel.setIconType(isOpen ? IconType.ARROW_DOWNWARD : IconType.ARROW_FORWARD);
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
    public HasText getQuery() {
        return query;
    }

    @Override
    public void displayLoadingResults(String message) {
        tab.selectTab("results");
        loadingResults.setText(message);
        showLoadingResults(true);
        onResize(null);
    }

    private void showLoadingResults(boolean display) {
        loadingResults.setVisible(display);
        resultsContainer.setVisible(!display);
    }

    @Override
    public void hideLoadingResults() {
        tab.selectTab("results");
        showLoadingResults(false);
        onResize(null);
    }

    private void createResultsTable() {
        // create table
        resultsTable = new DataGrid<Record>(1000, MyDataGridResources.INSTANCE);
        resultsTable.setSize("100%", "100%");
        resultsTable.setPageSize(20);

        resultsTable.setEmptyTableWidget(new Label("No results found..."));

        // add a pager
        SimplePager pager = new SimplePager();
        pager.setDisplay(resultsTable);

        // create the list data provider
        recordsList = new AsyncDataProvider<Record>() {
            @Override
            protected void onRangeChanged(HasData<Record> display) {
                final int start = display.getVisibleRange().getStart();
                int length = display.getVisibleRange().getLength();
                if(presenter != null) {
                    presenter.onRecordRangeChanged(start, length); //(int) (start / length));
                }
            }
        };
        recordsList.addDataDisplay(resultsTable);

        resultsTable.addCellPreviewHandler(new CellPreviewEvent.Handler<Record>() {
            @Override
            public void onCellPreview(CellPreviewEvent<Record> event) {
                if (BrowserEvents.MOUSEOVER.equals(event.getNativeEvent().getType())) {
                    outlineRecord(event.getValue());
                } else if (BrowserEvents.MOUSEOUT.equals(event.getNativeEvent().getType())) {
                    outlineRecord(null);
                }
            }
        });
/*
        sortDataHandler = new ColumnSortEvent.ListHandler<>(recordsList.getList());
        resultsTable.addColumnSortHandler(sortDataHandler);
*/
        // now add table to the results panel
        resultsPanel.setWidget(resultsTable);
        pagerPanel.setWidget(pager);

        SubrowTableBuilder tableBuilder = new SubrowTableBuilder<Record>(resultsTable) {

            @Override
            protected String getInformation(Record record) {
                return record.getContent() == null ? "No information" : record.getContent();
            }

        };
        resultsTable.setTableBuilder(tableBuilder);

        // define columns
        Column<Record, Boolean> checkColumn = new Column<Record, Boolean>(new MaterialCheckBoxCell()) {
            @Override
            public Boolean getValue(Record record) {
                return selectedRecord.contains(record);
            }
        };
        checkColumn.setFieldUpdater(new FieldUpdater<Record, Boolean>() {
            @Override
            public void update(int index, Record record, Boolean value) {
                if(selectedRecord.contains(record) != value) {
                    if(value) {
                        selectedRecord.add(record);
                    } else {
                        selectedRecord.remove(record);
                    }
                    resultsTable.redraw();
                    refreshMap();
                    enableQuotingMayBe();
                }
            }
        });
        Column<Record, String> geometryColumn = new Column<Record, String>(new ImageCell(16, 16) {
        }) {
            @Override
            public String getValue(Record record) {
                return record.getGeometryWKT() == null ? StyleResources.INSTANCE.info().getSafeUri().asString() :
                        StyleResources.INSTANCE.logoEOBroker().getSafeUri().asString();
            }
        };

/*
        // IMAGE
        Column<Record, MaterialImage> thumbnailColumn = new Column<Record, MaterialImage>(new MaterialImageCell()) {
            @Override
            public MaterialImage getValue(Record object) {
                MaterialImage img = new MaterialImage();
                img.setUrl(object.getThumbnail() == null ?
                                (object.getType() == Record.TYPE.ARCHIVE ? "./images/imageryNotAvailable.png" :
                                        object.getType() == Record.TYPE.PLANNEDACQ ? "./images/imageryPlannedAcq.png" :
                                        "./images/imageryFutureAcq.png") :
                        object.getThumbnail());
                img.setWidth("40px");
                img.setHeight("40px");
                //SET IMAGE TO CIRCLE
                img.setType(ImageType.DEFAULT);
                return img;
            }
        };
*/
        TextColumn<Record> titleColumn = new TextColumn<Record>() {
            @Override
            public String getValue(Record object) {
                return object == null || object.getTitle() == null ? "Unknown" : object.getTitle();
            }
        };
/*
        titleColumn.setSortable(true);
        sortDataHandler.setComparator(titleColumn, new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
*/

/*
        // ITEM NAME
        TextColumn<Record> dateColumn = new TextColumn<Record>() {

            private DateTimeFormat fmt = DateTimeFormat.getShortDateFormat();

            @Override
            public String getValue(Record imageProductDTO) {
                return fmt.format(imageProductDTO.getStart());
            }
        };
        dateColumn.setSortable(true);
        sortDataHandler.setComparator(dateColumn, new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                return o1.getStart().compareTo(o2.getStart());
            }
        });

        // set the row styles based on type of record
        resultsTable.setRowStyles(new RowStyles<Record>() {
            @Override
            public String getStyleNames(Record record, int rowIndex) {
                return record.getType() == Record.TYPE.ARCHIVE ? "eobroker-archiveProduct" :
                        record.getType() == Record.TYPE.PLANNEDACQ ? "eobroker-plannedAcqProduct" :
                        "eobroker-futureOpportunityProduct";
            }
        });
*/

        resultsTable.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        resultsTable.setColumnWidth(checkColumn, "30px");
        //resultsTable.addColumn(geometryColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        resultsTable.addColumn(titleColumn, "title");
        resultsTable.setColumnWidth(titleColumn, "100px");
/*
        resultsTable.addColumn(sensorColumn, "Sensor");
        resultsTable.setColumnWidth(sensorColumn, "100px");
        resultsTable.addColumn(dateColumn, "Acq date");
        resultsTable.setColumnWidth(dateColumn, "100px");
*/
        tableBuilder.addDeployableColumn(StyleResources.INSTANCE.info(), StyleResources.INSTANCE.info());

        // enable quote button
        enableQuotingMayBe();

    }

    @Override
    public void displayQueryResponse(SearchResponse searchResponse) {
        resultsPanel.getElement().getStyle().setProperty("height", (Window.getClientHeight() - 135 - queryPanel.getAbsoluteTop()) + "px");
        recordsList.updateRowCount(searchResponse.getTotalRecords(), true);
        List<Record> records = searchResponse.getRecords();
        recordsList.updateRowData(searchResponse.getStart(), records);
        boolean hasResults = searchResponse.getTotalRecords() > 0;
        resultsPanel.setVisible(hasResults);
        resultsMessage.setVisible(!hasResults);
        resultsMessage.setText(hasResults ? "Found " + searchResponse.getTotalRecords() + " results" : "No results found...");
        submitForQuote.setVisible(hasResults);
        // refresh map
        refreshMap();
    }

    private void refreshMap() {
        ArcgisMapJSNI arcgisMap = mapContainer.getArcgisMap();
        MapJSNI map = mapContainer.map;
        // refresh products display on map
        for(Record record : resultsTable.getVisibleItems()) {
            // skip if no geometry
            if(record.getGeometryWKT() == null) continue;
            boolean toRender = selectedRecord.contains(record);
            boolean rendered = renderedRecords.containsKey(record);
            if(toRender && !rendered) {
                RecordRendering recordRendering = new RecordRendering();
                String geometryWKT = record.getGeometryWKT();
                GeometryJSNI geometryJSNI = createGeometry(arcgisMap, geometryWKT);
                recordRendering.geometry = map.getGraphics().addGraphic(geometryJSNI,
                        arcgisMap.createFillSymbol("#00ffff", 2, "rgba(0,0,0,0.0)"));
                // add wms layer
/*
                if (record.getType() == Record.TYPE.ARCHIVE && record.getQl() != null) {
                    recordRendering.overlay = map.addWMSLayer(record.getQl(), WMSLayerInfoJSNI.createInfo(record.getSatelliteName(), record.getSatelliteName()), polygon.getExtent());
                }
*/
                renderedRecords.put(record, recordRendering);
            } else if(!toRender && rendered) {
                RecordRendering recordRendering = renderedRecords.get(record);
                if(recordRendering.geometry != null) {
                    map.getGraphics().remove(recordRendering.geometry);
                }
                if(recordRendering.overlay != null) {
                    map.removeWMSLayer(recordRendering.overlay);
                }
                renderedRecords.remove(record);
            }
        }
        // remove previous outline record
        if(outlinedRecordGraphicJSNI != null) {
            map.getGraphics().remove(outlinedRecordGraphicJSNI);
        }
        // add outlined record on top
        if(outlinedRecord != null) {
            if(outlinedRecord.getGeometryWKT() != null) {
                GeometryJSNI geometryJSNI = createGeometry(arcgisMap, outlinedRecord.getGeometryWKT());
                outlinedRecordGraphicJSNI = map.getGraphics().addGraphic(geometryJSNI,
                        arcgisMap.createFillSymbol("#0000ff", 2, "rgba(0,0,0,0.2)"));
            }
        }
    }

    private GeometryJSNI createGeometry(ArcgisMapJSNI arcgisMap, String geometryWKT) {
        switch (geometryWKT.substring(0, geometryWKT.indexOf("("))) {
            case "POLYGON":
                return arcgisMap.createPolygon(geometryWKT.replace("POLYGON((", "").replace("))", ""));
            default:
                return arcgisMap.createGeometry(geometryWKT);
        }
    }

    private void outlineRecord(Record record) {
        outlinedRecord = record;
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
        if(mapContainer != null) {
            mapContainer.clearAoI();
            clearRecordsSelection();
        }
    }

    @Override
    public HasClickHandlers getQuoteButton() {
        return submitForQuote;
    }

    @Override
    public List<Record> getSelectedRecord() {
        return new ArrayList<Record>(selectedRecord);
    }

    @Override
    public void clearRecordsSelection() {
        MapJSNI map = mapContainer.map;
        if(renderedRecords.size() > 0) {
            for(RecordRendering recordRendering : renderedRecords.values()) {
                map.getGraphics().remove(recordRendering.geometry);
                map.removeWMSLayer(recordRendering.overlay);
            }
            renderedRecords.clear();
            selectedRecord.clear();
        }
        if(outlinedRecord != null) {
            if(outlinedRecordGraphicJSNI != null) {
                map.getGraphics().remove(outlinedRecordGraphicJSNI);
            }
            outlinedRecord = null;
        }
        resultsTable.redraw();
        //recordsList.refresh();
    }

    @Override
    public void showQuery() {
        tab.selectTab("query");
    }

    @Override
    public void centerOnAoI() {
        mapContainer.centerOnAoI();
    }

    @Override
    public void setProductDatasetCatalogDTO(ProductDatasetCatalogueDTO productDatasetCatalogueDTO) {
        image.setUrl(productDatasetCatalogueDTO.getImageUrl());
        name.setText(productDatasetCatalogueDTO.getName());
        supplier.setText(productDatasetCatalogueDTO.getCompany().getName());
        protocol.setText(productDatasetCatalogueDTO.getDatasetStandard().getName());
        Scheduler.get().scheduleDeferred(() -> {
            tab.selectTab("query");
            onResize(null);
        });
    }

    @Override
    public void setParameters(List<FormElement> formElements) {
        additionalFields.clear();
        additionalFieldsPanel.setVisible(formElements.size() > 0);
        for(FormElement formElement : formElements) {
            ElementEditor editor = FormHelper.createEditor(formElement);
            editor.addStyleName(style.editor());
            MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
            materialColumn.add(editor);
            additionalFields.add(materialColumn);
        }
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                onResize(null);
            }
        });
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void onResize(ResizeEvent event) {
        searchPanel.onResize();
        if(mapContainer.map != null) {
            mapContainer.map.resize();
        }
    }

}