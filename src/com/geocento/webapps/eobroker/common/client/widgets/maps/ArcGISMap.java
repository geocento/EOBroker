package com.geocento.webapps.eobroker.common.client.widgets.maps;

import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.ArcgisMapJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.MapJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.MapResources;
import com.geocento.webapps.eobroker.common.shared.LatLng;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by thomas on 30/05/2016.
 */
public class ArcGISMap extends Composite implements RequiresResize {

    protected final MapContainer mapContainer = new MapContainer();

    public ArcgisMapJSNI arcgisMap;

    public ArcGISMap() {
        initWidget(mapContainer);
    }

    public void loadArcGISMap(final Callback<Void, Exception> callback) {
        final Callback<ArcgisMapJSNI, Exception> loadmapCallback = new Callback<ArcgisMapJSNI, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                callback.onFailure(reason);
            }

            @Override
            public void onSuccess(ArcgisMapJSNI result) {
                arcgisMap = result;
                callback.onSuccess(null);
            }
        };
        // check if already loaded
        if(!arcgisLoaded()) {
            LinkElement link = Document.get().createLinkElement();
            link.setRel("stylesheet");
            link.setHref("https://js.arcgis.com/3.18/esri/css/esri.css");
            nativeAttachToHead(link);

            ScriptInjector
                    .fromUrl("https://js.arcgis.com/3.18/")
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .setCallback(new Callback<Void, Exception>() {
                        @Override
                        public void onFailure(Exception reason) {
                            callback.onFailure(reason);
                        }

                        @Override
                        public void onSuccess(Void result) {
                            ScriptInjector
                                    .fromString(MapResources.INSTANCE.arcgisMap().getText())
                                    .setWindow(ScriptInjector.TOP_WINDOW)
                                    .inject();
                            loadMapLibs(loadmapCallback);
                        }
                    }).inject();
        } else {
            loadMapLibs(loadmapCallback);
        }
    }

    protected static native void nativeAttachToHead(JavaScriptObject scriptElement) /*-{
        $doc.getElementsByTagName("head")[0].appendChild(scriptElement);
    }-*/;

    private native boolean arcgisLoaded() /*-{
        return $wnd.arcgisMap != undefined;
    }-*/;

    private native JavaScriptObject loadMapLibs(Callback<ArcgisMapJSNI, Exception> callback) /*-{
        var success = function(map) {
            callback.@com.google.gwt.core.client.Callback::onSuccess(Ljava/lang/Object;)(map);
        }
        new $wnd.arcgisMap(success);
    }-*/;

    public void createMap(String basemap, LatLng latLng, int zoom, com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback<MapJSNI> callback) {
        arcgisMap.createMap(basemap, latLng.getLat(), latLng.getLng(), zoom, mapContainer.getElement(), callback);
    }

    public static class MapContainer extends FlowPanel implements RequiresResize {
        public void addVirtual(Widget w) {
            w.removeFromParent();
            getChildren().add(w);
            adopt(w);
        }

        /**
         * This method is automatically called by the parent container whenever it
         * changes size.
         */
        public void onResize() {
            for (Widget child : getChildren()) {
                if (child instanceof RequiresResize) {
                    ((RequiresResize) child).onResize();
                }
            }
        }
    }

    public void onResize() {
        mapContainer.onResize();
    }

}
