package com.geocento.webapps.eobroker.common.client.widgets.maps.resources;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by thomas on 01/06/2016.
 */
public class EditJSNI extends JavaScriptObject {

    protected EditJSNI() {
    }

    public final native void activate(GraphicJSNI graphicJSNI) /*-{
        this.activate($wnd['esri'].toolbars.Edit.EDIT_VERTICES, graphicJSNI);
    }-*/;

    public final native void deactivate() /*-{
        this.deactivate();
    }-*/;

    public final native void onDrawEnd(Callback<DrawEventJSNI> callback) /*-{
        var complete = function(event) {
            callback.@com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback::callback(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
        }
        this.on("draw-complete", complete);
    }-*/;

}
