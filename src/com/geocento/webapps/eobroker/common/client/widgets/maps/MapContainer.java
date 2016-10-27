package com.geocento.webapps.eobroker.common.client.widgets.maps;

import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.*;
import com.geocento.webapps.eobroker.common.shared.LatLng;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialAnchorButton;

/**
 * Created by thomas on 21/09/2016.
 */
public class MapContainer extends Composite {

    interface MapContainerUiBinder extends UiBinder<Widget, MapContainer> {
    }

    private static MapContainerUiBinder ourUiBinder = GWT.create(MapContainerUiBinder.class);

    public static interface Presenter {
        void aoiChanged(AoI aoi);
    }

    @UiField
    ArcGISMap mapContainer;
    @UiField
    MaterialAnchorButton drawPolygon;
    @UiField
    MaterialAnchorButton clearAoIs;

    private Presenter presenter;

    private Callback<Void, Exception> mapLoadedHandler = null;

    public MapJSNI map;

    private boolean mapLoaded = false;

    private GraphicJSNI aoiRendering;

    public MapContainer() {

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
                        map = mapJSNI;
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
                        map.setZoom(3);
                        mapLoaded();
                    }
                });
            }
        });

    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setWidth(String width) {
        mapContainer.setWidth(width);
    }

    public void setHeight(String height) {
        mapContainer.setHeight(height);
    }

    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        this.mapLoadedHandler = mapLoadedHandler;
        if(mapLoaded) {
            mapLoadedHandler.onSuccess(null);
        }
    }

    private void mapLoaded() {
        mapLoaded = true;
        if(mapLoadedHandler != null) {
            mapLoadedHandler.onSuccess(null);
        }
    }

    public void displayAoI(AoI aoi) {
        if(aoiRendering != null) {
            map.getGraphics().remove(aoiRendering);
        }
        if(aoi != null) {
            aoiRendering = map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometryFromAoI(aoi), mapContainer.arcgisMap.createFillSymbol("#ff00ff", 2, "rgba(0,0,0,0.2)"));
        }
    }
}