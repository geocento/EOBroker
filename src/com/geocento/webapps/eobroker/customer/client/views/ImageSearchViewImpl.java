package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.ArcGISMap;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.ArcgisMapJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.DrawEventJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.DrawJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.MapJSNI;
import com.geocento.webapps.eobroker.common.shared.LatLng;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.AoIPolygon;
import com.geocento.webapps.eobroker.common.shared.entities.ImageryService;
import com.geocento.webapps.eobroker.common.shared.imageapi.ImageProductDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import gwt.material.design.client.base.MaterialCheckBoxCell;
import gwt.material.design.client.base.MaterialImageCell;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.ImageType;
import gwt.material.design.client.constants.ProgressType;
import gwt.material.design.client.ui.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ImageSearchViewImpl extends Composite implements ImageSearchView {

    interface SearchPageUiBinder extends UiBinder<Widget, ImageSearchViewImpl> {
    }

    private static SearchPageUiBinder ourUiBinder = GWT.create(SearchPageUiBinder.class);

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
    MaterialNavBar navBar;
    @UiField
    MaterialTab tab;
    @UiField
    MaterialTextBox sensors;
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
    MaterialImage logo;

    private Presenter presenter;

    private Callback<Void, Exception> mapLoadedHandler = null;

    private MapJSNI map;

    private boolean mapLoaded = false;

    private CellTable<ImageProductDTO> resultsTable;

    private final ProvidesKey<ImageProductDTO> KEY_PROVIDER = new ProvidesKey<ImageProductDTO>() {
        @Override
        public Object getKey(ImageProductDTO item) {
            return item.getProductId();
        }
    };
    private final SelectionModel<ImageProductDTO> selectionModel = new MultiSelectionModel<ImageProductDTO>(KEY_PROVIDER);

    public ImageSearchViewImpl(ClientFactoryImpl clientFactory) {
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
        sensors.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.onSensorsChanged(event.getValue());
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
        navBar.showProgress(ProgressType.INDETERMINATE);
        searchBar.hide();
    }

    @Override
    public void hideLoadingResults() {
        navBar.hideProgress();
        searchBar.show();
        tab.selectTab("results");
    }

    @Override
    public void displayImageProducts(List<ImageProductDTO> imageProductDTOs) {
        // add products to map
/*
        map.getGraphics().clear();
*/
        for(ImageProductDTO imageProductDTO : imageProductDTOs) {
            AoIPolygon aoiPolygon = new AoIPolygon();
            aoiPolygon.setWktRings(imageProductDTO.getCoordinatesWKT().replace("POLYGON((", "").replace("))", ""));
            map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometryFromAoI(aoiPolygon), mapContainer.arcgisMap.createFillSymbol("#ff00ff", 2, "rgba(0,0,0,0.2)"));
        }

        // add products to results table

        // define columns
        ColumnSortEvent.ListHandler<ImageProductDTO> sortDataHandler = new ColumnSortEvent.ListHandler<>(new ArrayList<ImageProductDTO>());
        Column<ImageProductDTO, Boolean> checkColumn = new Column<ImageProductDTO, Boolean>(new MaterialCheckBoxCell()) {
            @Override
            public Boolean getValue(ImageProductDTO object) {
                return true; //selectionModel.isSelected(object);
            }
        };
/*
        checkColumn.setFieldUpdater(new FieldUpdater<ImageProductDTO, Boolean>() {
            @Override
            public void update(int index, ImageProductDTO object, Boolean value) {
                selectionModel.setSelected(object, value);
            }
        });
*/

        // IMAGE
        Column<ImageProductDTO, MaterialImage> thumbnailColumn = new Column<ImageProductDTO, MaterialImage>(new MaterialImageCell()) {
            @Override
            public MaterialImage getValue(ImageProductDTO object) {
                MaterialImage img = new MaterialImage();
                img.setUrl(object.getThumbnail());
                img.setWidth("40px");
                img.setHeight("40px");
                //SET IMAGE TO CIRCLE
                img.setType(ImageType.DEFAULT);
                return img;
            }
        };

        TextColumn<ImageProductDTO> sensorColumn = new TextColumn<ImageProductDTO>() {
            @Override
            public String getValue(ImageProductDTO object) {
                return object.getSatelliteName();
            }
        };
        sensorColumn.setSortable(true);

        // ITEM NAME
        TextColumn<ImageProductDTO> dateColumn = new TextColumn<ImageProductDTO>() {

            private DateTimeFormat fmt = DateTimeFormat.getShortDateFormat();

            @Override
            public String getValue(ImageProductDTO imageProductDTO) {
                return fmt.format(imageProductDTO.getStart());
            }
        };
        dateColumn.setSortable(true);
        sortDataHandler.setComparator(dateColumn, new Comparator<ImageProductDTO>() {
            @Override
            public int compare(ImageProductDTO o1, ImageProductDTO o2) {
                return o1.getStart().compareTo(o2.getStart());
            }
        });
        // create table
        resultsTable = new CellTable<ImageProductDTO>(100, KEY_PROVIDER);
        resultsTable.setSize("100%", "40vh");
        resultsTable.setStyleName("striped");
        resultsTable.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        resultsTable.setColumnWidth(checkColumn, "40px");
        resultsTable.addColumn(thumbnailColumn, "Thumb");
        resultsTable.setColumnWidth(thumbnailColumn, "60px");
        resultsTable.addColumn(sensorColumn, "Sensor");
        resultsTable.setColumnWidth(sensorColumn, "120px");
        resultsTable.addColumn(dateColumn, "Acq date");
        resultsTable.setColumnWidth(dateColumn, "120px");
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(resultsTable);

        // now add table to the results panel

        resultsPanel.setWidget(resultsTable);
        resultsPanel.getElement().getStyle().setProperty("maxHeight", (Window.getClientHeight() - 50 - queryPanel.getAbsoluteTop()) + "px");

        // create the list data provider
        ListDataProvider<ImageProductDTO> imagesList = new ListDataProvider<ImageProductDTO>(imageProductDTOs);
        imagesList.addDataDisplay(resultsTable);

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
    public void setSuppliers(List<ImageryService> imageryServices) {
        providerDropdown.clear();
        for(final ImageryService imageryService : imageryServices) {
            MaterialLink materialLink = new MaterialLink(ButtonType.RAISED, imageryService.getName(), new MaterialIcon(IconType.ADD_A_PHOTO));
            materialLink.setBackgroundColor("white");
            materialLink.setTextColor("black");
            materialLink.setTooltip(imageryService.getDescription());
            materialLink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.onProviderChanged(imageryService);
                }
            });
            providerDropdown.add(materialLink);
        }
    }

    @Override
    public void displaySupplier(ImageryService imageryService) {
        providersLink.setText(imageryService.getName());
    }

    @Override
    public HasClickHandlers getUpdateButton() {
        return update;
    }

    @Override
    public HasClickHandlers getHomeButton() {
        return logo;
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
    public Widget asWidget() {
        return this;
    }

}