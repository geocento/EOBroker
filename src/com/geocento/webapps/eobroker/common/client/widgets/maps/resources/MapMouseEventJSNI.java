package com.geocento.webapps.eobroker.common.client.widgets.maps.resources;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by thomas on 01/06/2016.
 */
public class MapMouseEventJSNI extends JavaScriptObject {

    protected MapMouseEventJSNI() {
    }

    public final native PointJSNI getMapPoint() /*-{
        return this.mapPoint;
    }-*/;

    public final native PointJSNI getScreenPoint() /*-{
        return this.screenPoint;
    }-*/;

}
