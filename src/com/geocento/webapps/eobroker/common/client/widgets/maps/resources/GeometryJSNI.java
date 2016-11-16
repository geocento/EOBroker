package com.geocento.webapps.eobroker.common.client.widgets.maps.resources;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by thomas on 30/05/2016.
 */
public class GeometryJSNI extends JavaScriptObject {

    protected GeometryJSNI() {
    }

    public final native String getType() /*-{
        return this.type;
    }-*/;

    public final native JavaScriptObject toJson() /*-{
        return this.toJson();
    }-*/;

    public final native void setSpatialReference(int wktid) /*-{
        this.setSpatialReference(new $wnd['esri'].SpatialReference(wktid));
    }-*/;

    public final native ExtentJSNI getExtent() /*-{
        return this.getExtent();
    }-*/;

}
