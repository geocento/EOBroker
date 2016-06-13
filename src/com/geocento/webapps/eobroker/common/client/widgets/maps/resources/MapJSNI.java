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

    public final native void removeAllLayers() /*-{
        this.removeAllLayers();
    }-*/;

}
