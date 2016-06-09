package com.geocento.webapps.eobroker.client.widgets.maps.resources;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by thomas on 01/06/2016.
 */
public class GraphicsLayerJSNI extends JavaScriptObject {

    protected GraphicsLayerJSNI() {
    }

    public final native GraphicJSNI addGraphic(GeometryJSNI geometry, SymbolJSNI symbol) /*-{
        return this.add(new $wnd["esri"].Graphic(geometry, symbol));
    }-*/;

    public final native void clear() /*-{
        this.clear();
    }-*/;

}
