package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.ArcGISMap;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.ArcgisMapJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.DrawEventJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.DrawJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.MapJSNI;
import com.geocento.webapps.eobroker.common.shared.LatLng;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;

/**
 * Created by thomas on 09/05/2016.
 */
public class RequestImageryViewImpl extends Composite implements RequestImageryView {

    private Presenter presenter;

    private boolean mapRevealed = true;

    interface RequestImageryUiBinder extends UiBinder<Widget, RequestImageryViewImpl> {
    }

    private static RequestImageryUiBinder ourUiBinder = GWT.create(RequestImageryUiBinder.class);

    @UiField
    MaterialLink signIn;
    @UiField
    MaterialNavBar navBar;
    @UiField
    ArcGISMap mapContainer;
    @UiField
    MaterialAnchorButton drawPolygon;
    @UiField
    MaterialAnchorButton clearAoIs;
    @UiField
    MaterialRow mapPanel;
    @UiField
    MaterialButton closeMap;
    @UiField
    MaterialTitle title;
    @UiField
    HTMLPanel suppliers;
    @UiField
    MaterialImage logo;

    private Callback<Void, Exception> mapLoadedHandler = null;

    public MapJSNI map;

    private boolean mapLoaded = false;

    public RequestImageryViewImpl(final ClientFactoryImpl clientFactory) {
        initWidget(ourUiBinder.createAndBindUi(this));
        mapContainer.setHeight((Window.getClientHeight() - 64) + "px");
        revealMap(false, false);
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
                        RequestImageryViewImpl.this.map = mapJSNI;
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

    @Override
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
            revealMap(true, false);
        }
    }

    private void revealMap(final boolean display, boolean animated) {
        final int screenHeight = Window.getClientHeight() - 64;
        if(mapRevealed != display) {
            if(animated) {
                if(display) {
                    mapPanel.setVisible(true);
                }
                new Animation() {

                    @Override
                    protected void onUpdate(double progress) {
                        mapPanel.getElement().getStyle().setMarginTop(-1 * screenHeight * (display ? (1 - progress) : progress), Style.Unit.PX);
                    }

                    @Override
                    protected void onComplete() {
                        mapRevealed = display;
                        mapPanel.setVisible(display);
                    }
                }.run(500);
            } else {
                mapPanel.getElement().getStyle().setMarginTop(display ? 0 : -1 * screenHeight, Style.Unit.PX);
                mapPanel.setVisible(display);
                mapRevealed = display;
            }
        }
        //MaterialAnimator.animate(display ? Transition.SLIDEINDOWN : Transition.SLIDEOUTUP, mapContainer, 300);
    }

    @Override
    public void displayAoI(AoI aoi) {
        map.getGraphics().clear();
        if(aoi != null) {
            map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometryFromAoI(aoi), mapContainer.arcgisMap.createFillSymbol("#ff00ff", 2, "rgba(0,0,0,0.2)"));
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displaySearchError(String message) {
        MaterialToast.fireToast(message);
    }

    @Override
    public void setDescription(String description) {
        this.title.setDescription(description);
    }

    @UiHandler("closeMap")
    void closeMap(ClickEvent clickEvent) {
        revealMap(false, true);
    }

    @Override
    public HasClickHandlers getHomeButton() {
        return logo;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}