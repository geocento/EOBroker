package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.ArcGISMap;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.ArcgisMapJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.DrawEventJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.DrawJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.MapJSNI;
import com.geocento.webapps.eobroker.common.shared.imageapi.ImageProductDTO;
import com.geocento.webapps.eobroker.common.shared.LatLng;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.AoIPolygon;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialAnchorButton;
import gwt.material.design.client.ui.MaterialDropDown;
import gwt.material.design.client.ui.MaterialLink;

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

    private Presenter presenter;

    private Callback<Void, Exception> mapLoadedHandler = null;

    private MapJSNI map;

    private boolean mapLoaded = false;

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

    }

    @Override
    public void hideLoadingResults() {

    }

    @Override
    public void displayImageProducts(List<ImageProductDTO> imageProductDTOs) {
        map.removeAllLayers();
        for(ImageProductDTO imageProductDTO : imageProductDTOs) {
            AoIPolygon aoiPolygon = new AoIPolygon();
            aoiPolygon.setWktRings(imageProductDTO.getCoordinatesWKT());
            map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometryFromAoI(aoiPolygon), mapContainer.arcgisMap.createFillSymbol("#ff00ff", 2, "rgba(0,0,0,0.2)"));
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displaySupplier(String name, String imageUrl) {

    }

    @Override
    public Widget asWidget() {
        return this;
    }

}