package com.geocento.webapps.eobroker.common.client.widgets.maps;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.*;
import com.geocento.webapps.eobroker.common.shared.LatLng;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.html.ListItem;

import java.util.Date;

/**
 * Created by thomas on 21/09/2016.
 */
public class MapContainer extends Composite {

    interface MapContainerUiBinder extends UiBinder<Widget, MapContainer> {
    }

    private static MapContainerUiBinder ourUiBinder = GWT.create(MapContainerUiBinder.class);

    public static interface Presenter {
        void aoiChanged(AoIDTO aoi);
    }

    public static interface Style extends CssResource {

        String fabButton();
    }

    @UiField
    Style style;

    @UiField
    ArcGISMap mapContainer;
    @UiField
    MaterialAnchorButton drawPolygon;
    @UiField
    MaterialButton clearAoIs;
    @UiField
    MaterialButton fabButton;
    @UiField
    MaterialAnchorButton uploadFile;
    @UiField
    MaterialPanel panel;
    @UiField
    HTMLPanel search;
    @UiField
    MaterialButton editGeometry;
    @UiField
    MaterialFAB addButton;
    @UiField
    MaterialAnchorButton drawExtent;
    @UiField
    MaterialFABList addButtons;
    @UiField
    protected MaterialAnchorButton selectButton;

    private Presenter presenter;

    private Callback<Void, Exception> mapLoadedHandler = null;

    public MapJSNI map;

    private boolean mapLoaded = false;

    private DrawJSNI drawJSNI;

    private EditJSNI editJSNI;

    private SearchJSNI searchJSNI;

    private GraphicJSNI aoiRendering;

    protected AoIDTO aoi;

    private boolean isEditing = false;

    private boolean editable = true;

    public MapContainer() {

        initWidget(ourUiBinder.createAndBindUi(this));

        setEdit(false);
        displayLoading();
        mapContainer.loadArcGISMap(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                hideLoading();
            }

            @Override
            public void onSuccess(Void result) {
                hideLoading();
                mapContainer.createMap("streets", new LatLng(40.0, -4.0), 3, new com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback<MapJSNI>() {

                    @Override
                    public void callback(final MapJSNI mapJSNI) {
                        final ArcgisMapJSNI arcgisMap = mapContainer.arcgisMap;
                        map = mapJSNI;

                        setEdit(editable);
                        updateButtons();

                        drawJSNI = arcgisMap.createDraw(mapJSNI);
                        drawJSNI.onDrawEnd(new com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback<DrawEventJSNI>() {

                            @Override
                            public void callback(DrawEventJSNI result) {
                                drawJSNI.deactivate();
                                AoIDTO aoi = AoIUtil.createAoI(arcgisMap.convertsToGeographic(result.getGeometry()));
                                //aoi.setName("Drawn AoI - " + DateUtils.formatDateTime(new Date()));
                                displayAoI(aoi);
                                if(presenter != null) {
                                    presenter.aoiChanged(aoi);
                                }
                            }
                        });

                        editJSNI = arcgisMap.createEdit(mapJSNI);
                        mapJSNI.onEvent("click", new com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback<JavaScriptObject>() {
                            @Override
                            public void callback(JavaScriptObject result) {
                                stopEditing();
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

    public void setHeight(String height) {
        super.setHeight(height);
/*
        boolean large = getOffsetHeight() > 450;
        fabButton.setSize(large ? ButtonSize.LARGE : ButtonSize.MEDIUM);
        IconSize iconSize = large ? IconSize.MEDIUM : IconSize.SMALL;
        fabButton.setIconSize(iconSize);
        clearAoIs.setIconSize(iconSize);
        drawPolygon.setIconSize(iconSize);
        uploadFile.setIconSize(iconSize);
*/
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

    public void displayAoI(AoIDTO aoi) {
        if(aoiRendering != null) {
            map.getGraphics().remove(aoiRendering);
        }
        if(aoi != null) {
            aoiRendering = map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometryFromAoI(aoi), mapContainer.arcgisMap.createFillSymbol("#ff00ff", 2, "rgba(0,0,0,0.2)"));
        }
        this.aoi = aoi;
        updateButtons();
    }

    public AoIDTO getAoi() {
        return aoi;
    }

    public void centerOnAoI() {
        if(aoi != null) {
            map.setExtent(mapContainer.arcgisMap.createGeometryFromAoI(aoi).getExtent());
        }
    }

    public ArcgisMapJSNI getArcgisMap() {
        return mapContainer.arcgisMap;
    }

    public void setEdit(boolean display) {
        addButton.setVisible(display);
        if(!display) {
            editGeometry.setVisible(false);
            clearAoIs.setVisible(false);
        }
    }

    public void clearAoI() {
        // make sure we stop editing before
        stopEditing();
        if(map != null && aoiRendering != null) {
            map.getGraphics().remove(aoiRendering);
            aoi = null;
            aoiRendering = null;
        }
        updateButtons();
    }

    private void updateButtons() {
        boolean hasAoI = aoiRendering != null;
        editGeometry.setVisible(hasAoI);
        clearAoIs.setVisible(hasAoI);
    }

    @UiHandler("drawExtent")
    public void drawExtent(ClickEvent clickEvent) {
        clearAoI();
        startDrawing("rectangle");
    }

    @UiHandler("editGeometry")
    public void editGeometry(ClickEvent clickEvent) {
        if(aoiRendering != null) {
            startEditing(aoiRendering);
        }
    }

    @UiHandler("drawPolygon")
    void drawPolygon(ClickEvent clickEvent) {
        clearAoI();
        drawJSNI.activate("polygon");
        drawPolygon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            }
        });
    }

    @UiHandler("clearAoIs")
    void clearAoIs(ClickEvent clickEvent) {
        clearAoI();
        if(presenter != null) {
            presenter.aoiChanged(null);
        }
    }

    @UiHandler("uploadFile")
    void uploadFile(ClickEvent clickEvent) {
        UploadAoI.getInstance().display(new UploadAoI.Presenter() {
            @Override
            public void aoiSelected(AoIDTO aoIDTO) {
                displayAoI(aoIDTO);
                centerOnAoI();
                presenter.aoiChanged(aoIDTO);
            }
        });
    }

    public void startDrawing(String aoitype) {
        drawJSNI.activate(aoitype);
    }

    public void startEditing(GraphicJSNI graphicJSNI) {
        editJSNI.activate(graphicJSNI);
        isEditing = true;
    }

    public void stopEditing() {
        editJSNI.deactivate();
        // if we were actually editing, make sure we update the AoI
        if(isEditing) {
            aoi.setWktGeometry("POLYGON((" + ((PolygonJSNI) aoiRendering.getGeometry()).getRings() + "))");
            if(presenter != null) {
                presenter.aoiChanged(aoi);
            }
            isEditing = false;
        }
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    protected void addButton(MaterialButton materialButton, String message) {
        materialButton.setLayoutPosition(com.google.gwt.dom.client.Style.Position.ABSOLUTE);
        materialButton.setBottom(30);
        materialButton.setRight(160);
        materialButton.setType(ButtonType.FLOATING);
        materialButton.addStyleName(style.fabButton());
        MaterialTooltip materialTooltip = new MaterialTooltip(materialButton);
        materialTooltip.setText(message);
        materialTooltip.setPosition(Position.BOTTOM);
        panel.add(materialButton);
    }

    protected void addFABButton(MaterialAnchorButton materialButton, String tooltipMessage) {
        MaterialTooltip materialTooltip = new MaterialTooltip(materialButton);
        materialTooltip.setText(tooltipMessage);
        materialTooltip.setPosition(Position.LEFT);
        materialButton.setType(ButtonType.FLOATING);
        materialButton.setWaves(WavesType.LIGHT);
        materialButton.setIconSize(IconSize.SMALL);
        addButtons.insert(new ListItem(materialButton), 0);
    }

    protected void displayLoading() {
        MaterialLoader.showLoading(true, panel);
    }

    protected void hideLoading() {
        MaterialLoader.showLoading(false, panel);
    }

}