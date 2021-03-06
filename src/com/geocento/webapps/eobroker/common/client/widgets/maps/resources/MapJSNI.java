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

/*
    public final native void removeAllLayers() */
/*-{
        this.removeAllLayers();
    }-*//*
;
*/

    public final native void resize() /*-{
        this.resize();
    }-*/;

    public final native void setZoom(int zoom) /*-{
        this.setZoom(zoom);
    }-*/;

    public final WMSLayerJSNI addWMSLayer(String wmsUrl, WMSLayerInfoJSNI info, ExtentJSNI extent) {
        return addWMSLayer(wmsUrl, info, extent, null);
    };

    public final WMSLayerJSNI addWMSLayer(String wmsUrl, WMSLayerInfoJSNI info, ExtentJSNI extent, String styleName) {
        return addWMSLayer(wmsUrl, info, extent, null, null);
    };

    public final native WMSLayerJSNI addWMSLayer(String wmsUrl, WMSLayerInfoJSNI info, ExtentJSNI extent, String styleName, String version) /*-{
        var wmsLayer = new $wnd['esri'].layers.WMSLayer(wmsUrl, {
            resourceInfo: {layerInfos: [info], extent: extent},
            visibleLayers: [info.name]
        });
        wmsLayer.spatialReferences[0] = 3857;
        if(styleName) {
            wmsLayer.customLayerParameters = {'styles': styleName};
        }
        if(version) {
            wmsLayer.version = version;
        }
        this.addLayers([wmsLayer]);
        return wmsLayer;
    }-*/;

    public final WMSLayerJSNI addWMSLayer(String wmsUrl, String layerName, ExtentJSNI extentJSNI) {
        if(extentJSNI == null) {
            extentJSNI = createExtent(-180.0, -90.0, 180.0, 90.0);
        }
        return addWMSLayer(wmsUrl, WMSLayerInfoJSNI.createInfo(layerName, layerName), extentJSNI);
    }

    public final native void removeWMSLayer(WMSLayerJSNI layer) /*-{
        this.removeLayer(layer);
    }-*/;

    public static final ExtentJSNI createExtent(double west, double south, double east, double north) {
        return createExtent(west, south, east, north, 4326);
    }

    public static final native ExtentJSNI createExtent(double minx, double miny, double maxx, double maxy, int wkid) /*-{
        return new $wnd['esri'].geometry.Extent(minx, miny, maxx, maxy, $wnd['esri'].SpatialReference(wkid));
    }-*/;

    public final native void setExtent(ExtentJSNI extent) /*-{
        this.setExtent(extent, true);
    }-*/;

    public final native void onEvent(String eventName, Callback<JavaScriptObject> callback) /*-{
        var complete = function(event) {
            callback.@com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback::callback(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
        }
        this.on(eventName, complete);
    }-*/;

    public final native PointJSNI toMap(double[] screenCoords) /*-{
        return this.toMap(new $wnd['esri'].geometry.ScreenPoint(screenCoords));
    }-*/;
}
