package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.styles.MyDataGridResources;
import com.geocento.webapps.eobroker.common.client.utils.opensearch.Record;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLabelIcon;
import com.geocento.webapps.eobroker.common.client.widgets.forms.ElementEditor;
import com.geocento.webapps.eobroker.common.client.widgets.forms.FormHelper;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.*;
import com.geocento.webapps.eobroker.common.client.widgets.table.celltable.SubrowTableBuilder;
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
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;
import gwt.material.design.client.constants.Color;
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

    private Presenter presenter;

    private Record outlinedRecord;

    private GraphicJSNI outlinedRecordGraphicJSNI;

    private DataGrid<Record> resultsTable;

    private ListDataProvider<Record> recordsList;

    private ColumnSortEvent.ListHandler<Record> sortDataHandler;

    private HashSet<Record> selectedRecord = new HashSet<Record>();

    private class RecordRendering {
        GraphicJSNI footprint;
        WMSLayerJSNI overlay;
    }

    private HashMap<Record, RecordRendering> renderedRecords = new HashMap<Record, RecordRendering>();

    private static NumberFormat format = NumberFormat.getFormat("#.##");

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

        mapContainer.setPresenter(aoi -> presenter.aoiChanged(aoi));

        Scheduler.get().scheduleDeferred(() -> {
            tab.selectTab("query");
            onResize(null);
        });

        Window.addResizeHandler(this);
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
        onResize(null);
    }

    @Override
    public void hideLoadingResults() {
        tab.selectTab("results");
        onResize(null);
    }

    private void createResultsTable() {
        // create table
        resultsTable = new DataGrid<Record>(1000, MyDataGridResources.INSTANCE);
        resultsTable.setSize("100%", "100%");
        // create the list data provider
        recordsList = new ListDataProvider<Record>();
        recordsList.setList(new ArrayList<Record>());
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
        sortDataHandler = new ColumnSortEvent.ListHandler<>(recordsList.getList());
        resultsTable.addColumnSortHandler(sortDataHandler);
        // now add table to the results panel
        resultsPanel.setWidget(resultsTable);

        SubrowTableBuilder tableBuilder = new SubrowTableBuilder<Record>(resultsTable) {
            @Override
            protected String getInformation(Record record) {
                String htmlContent = "";
                HashMap<String, String> properties = record.getProperties();
                for(String propertyName : properties.keySet()) {
                    htmlContent += addProperty(propertyName, properties.get(propertyName));
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
                return "<div style='padding: 5px; white-space: nowrap; text-overflow: ellipsis;'><b>" + name + ": </b>" + (value == null ? "NA" : value.toString()) + "</div>";
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
                return object.getTitle();
            }
        };
        titleColumn.setSortable(true);
        sortDataHandler.setComparator(titleColumn, new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

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
    public void displayQueryResponse(List<Record> records) {
        resultsPanel.getElement().getStyle().setProperty("height", (Window.getClientHeight() - 135 - queryPanel.getAbsoluteTop()) + "px");
        recordsList.getList().clear();
        recordsList.getList().addAll(records == null ? new ArrayList<Record>() : records);
        recordsList.refresh();
        // refresh map
        refreshMap();
    }

    private void refreshMap() {
        ArcgisMapJSNI arcgisMap = mapContainer.getArcgisMap();
        MapJSNI map = mapContainer.map;
        // refresh products display on map
        for(Record record : recordsList.getList()) {
            boolean toRender = selectedRecord.contains(record);
            boolean rendered = renderedRecords.containsKey(record);
            if(toRender && !rendered) {
                RecordRendering recordRendering = new RecordRendering();
                String geometryWKT = record.getGeometryWKT();
                GeometryJSNI geometryJSNI = createGeometry(arcgisMap, geometryWKT);
                recordRendering.footprint = map.getGraphics().addGraphic(geometryJSNI,
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
                if(recordRendering.footprint != null) {
                    map.getGraphics().remove(recordRendering.footprint);
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
            GeometryJSNI geometryJSNI = createGeometry(arcgisMap, outlinedRecord.getGeometryWKT());
            outlinedRecordGraphicJSNI = map.getGraphics().addGraphic(geometryJSNI,
                    arcgisMap.createFillSymbol("#0000ff", 2, "rgba(0,0,0,0.2)"));
        }
    }

    private GeometryJSNI createGeometry(ArcgisMapJSNI arcgisMap, String geometryWKT) {
        switch (geometryWKT.substring(0, geometryWKT.indexOf("("))) {
            case "POLYGON":
                return arcgisMap.createPolygon(geometryWKT.replace("POLYGON((", "").replace("))", ""));
        }

        return null;
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
                map.getGraphics().remove(recordRendering.footprint);
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
        recordsList.refresh();
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
    }

    @Override
    public void setParameters(List<FormElement> formElements) {
        additionalFields.clear();
        for(FormElement formElement : formElements) {
            ElementEditor editor = FormHelper.createEditor(formElement);
            editor.addStyleName(style.editor());
            MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
            materialColumn.add(editor);
            additionalFields.add(materialColumn);
        }
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