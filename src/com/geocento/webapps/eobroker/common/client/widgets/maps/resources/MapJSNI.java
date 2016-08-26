package com.geocento.webapps.eobroker.common.client.widgets.maps.resources;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by thomas on 30/05/2016.
 */
public class MapJSNI extends JavaScriptObject {

    protected MapJSNI() {
    }

    public final native GraphicsLayerJSNI getGraphics() /*-{
        return this.graphics;
    }-*/;

    public final native void removeAllLayers() /*-{
        this.removeAllLayers();
    }-*/;

    public final native void resize() /*-{
        this.resize();
    }-*/;

    public final native void setZoom(int zoom) /*-{
        this.setZoom(zoom);
    }-*/;

    public final native WMSLayerJSNI addWMSLayer(String wmsUrl, WMSLayerInfoJSNI info, ExtentJSNI extent) /*-{
        var wmsLayer = new $wnd['esri'].layers.WMSLayer(wmsUrl, {
            resourceInfo: {layerInfos: [info], extent: extent},
            visibleLayers: [info.name]
        });
        this.addLayers([wmsLayer]);
        return wmsLayer;
    }-*/;

    public final native void removeWMSLayer(WMSLayerJSNI layer) /*-{
        this.removeLayer(layer);
    }-*/;

}
