package com.geocento.webapps.eobroker.client.widgets.maps.resources;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by thomas on 30/05/2016.
 */
public class DrawEventJSNI extends JavaScriptObject {

    protected DrawEventJSNI() {
    }

    public final native GeometryJSNI getGeometry() /*-{
        return this.geometry;
    }-*/;

}
