package com.geocento.webapps.eobroker.common.client.widgets.maps.resources;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by thomas on 23/08/2016.
 */
public class WMSLayerJSNI extends JavaScriptObject {

    protected WMSLayerJSNI() {
    }

    public final native void setOpacity(double opacity) /*-{
        this.setOpacity(opacity);
    }-*/;

}
