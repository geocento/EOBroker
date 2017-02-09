package com.geocento.webapps.eobroker.common.client.widgets.maps.resources;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
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

    public final native EditJSNI createEdit(MapJSNI map) /*-{
        return this.createEdit(map);
    }-*/;

    public final native void createBaseMaps(MapJSNI map, Element element) /*-{
        this.createBaseMaps(map, element);
    }-*/;

    public final native SearchJSNI addSearch(MapJSNI map, Element element) /*-{
        return this.addSearch(map, element);
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

    public final native PolygonJSNI createPolygon(String wktRings) /*-{
        return this.createPolygon(wktRings);
    }-*/;

    public final native GeometryJSNI createGeometry(String wktString) /*-{
        return this.createGeometry(wktString);
    }-*/;

    public final native GeometryJSNI createExtent(double xmin, double ymin, double xmax, double ymax) /*-{
        return this.createExtent(xmin, ymin, xmax, ymax);
    }-*/;

    public final GeometryJSNI createGeometryFromAoI(AoIDTO aoi) {
        return createGeometry(aoi.getWktGeometry());
    }

    public final native GeometryJSNI convertsToGeographic(GeometryJSNI geometry) /*-{
        return $wnd['esri'].geometry.webMercatorToGeographic(geometry);
    }-*/;

    public final native void createBaseMapToggle(MapJSNI map, Element element) /*-{
        this.createBaseMapToggle(map, element);
    }-*/;

}
