package com.geocento.webapps.eobroker.common.client.widgets.maps.resources;

/**
 * Created by thomas on 01/06/2016.
 */
public class PolygonJSNI extends GeometryJSNI {

    protected PolygonJSNI() {
    }

    public final native String getRings() /*-{
        var rings = this.rings[0];
        var wktString = "", index;
        for(index = 0; index < rings.length; index++) {
            wktString += rings[index][0] + ' ' + rings[index][1] + ",";
        }
        // remove trailing comma
        return wktString.slice(0, wktString.length - 1);
    }-*/;

}
