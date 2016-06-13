package com.geocento.webapps.eobroker.common.client.widgets.maps.resources;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * Created by thomas on 30/05/2016.
 */
public class ViewJSNI extends JavaScriptObject {

    protected ViewJSNI() {
    }

    public final native void addWidget(Element element, String position) /*-{
        this.ui.add(element, position);
    }-*/;

}
