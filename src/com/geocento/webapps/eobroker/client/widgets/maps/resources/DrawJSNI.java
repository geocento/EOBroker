package com.geocento.webapps.eobroker.client.widgets.maps.resources;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by thomas on 01/06/2016.
 */
public class DrawJSNI extends JavaScriptObject {

    static public enum GEOMETRY_TYPE {LINE, POINT, POLYGON, POLYLINE, RECTANGLE};

    protected DrawJSNI() {
    }

    public final native void activate(String geometry_type) /*-{
        this.activate(geometry_type);
    }-*/;

    public final native void deactivate() /*-{
        this.deactivate();
    }-*/;

    public final native void onDrawEnd(Callback<DrawEventJSNI> callback) /*-{
        var complete = function(event) {
            callback.@com.geocento.webapps.eobroker.client.widgets.maps.resources.Callback::callback(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
        }
        this.on("draw-complete", complete);
    }-*/;

}
