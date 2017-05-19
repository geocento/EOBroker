package com.geocento.webapps.eobroker.common.client.widgets.maps.resources;

/**
 * Created by thomas on 01/06/2016.
 */
public class PointJSNI extends GeometryJSNI {

    protected PointJSNI() {
    }

    public final native void setY(double y) /*-{
        this.y = y;
    }-*/;

    public final native double getY() /*-{
        return this.y;
    }-*/;

    public final native void setX(double x) /*-{
        this.x = x;
    }-*/;

    public final native double getX() /*-{
        return this.x;
    }-*/;

}
