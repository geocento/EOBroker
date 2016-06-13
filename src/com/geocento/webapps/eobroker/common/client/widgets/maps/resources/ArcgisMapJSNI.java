package com.geocento.webapps.eobroker.common.client.widgets.maps.resources;

import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.AoIPolygon;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * Created by thomas on 30/05/2016.
 */
public class ArcgisMapJSNI extends JavaScriptObject {

    protected ArcgisMapJSNI() {
    }

    public final native void setDrawHandler(Callback<DrawEventJSNI> callback) /*-{
        var success = function(event) {
            callback.@com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback::callback(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
        }
        this.setDrawHandler(callback);
    }-*/;

    public final native MapJSNI createMap(String baseMap, double lat, double lng, int zoom, Element mapContainer, Callback<MapJSNI> callback) /*-{
        var onload = function(map) {
            callback.@com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback::callback(Lcom/google/gwt/core/client/JavaScriptObject;)(map);
        }
        this.createMap(baseMap, lat, lng, zoom, mapContainer, onload);
    }-*/;

    public final native DrawJSNI createDraw(MapJSNI map) /*-{
        return this.createDraw(map);
    }-*/;

    public final native void template() /*-{
    }-*/;

    public final native void createPointSymbol() /*-{
    }-*/;

    public final native LineSymbolJSNI createLineSymbol(String color, int width) /*-{
        return this.createLineSymbol(color, width);
    }-*/;

    public final native FillSymbolJSNI createFillSymbol(String strokeColor, int strokeWidth, String fillColor) /*-{
        return this.createFillSymbol(strokeColor, strokeWidth, fillColor);
    }-*/;

    public final native GeometryJSNI createPolygon(String wktRings) /*-{
        return this.createPolygon(wktRings);
    }-*/;

    public final GeometryJSNI createGeometryFromAoI(AoI aoi) {
        if(aoi instanceof AoIPolygon) {
            return createPolygon(((AoIPolygon) aoi).getWktRings());
        }
        return null;
    }

    public final native GeometryJSNI convertsToGeographic(GeometryJSNI geometry) /*-{
        return $wnd['esri'].geometry.webMercatorToGeographic(geometry);
    }-*/;
}
