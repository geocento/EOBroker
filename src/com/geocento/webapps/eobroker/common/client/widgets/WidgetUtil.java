package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.dom.client.Element;

/**
 * Created by thomas on 15/06/2016.
 */
public class WidgetUtil {

    public static native boolean isActiveElement(Element element) /*-{
        return document.activeElement === element;
    }-*/;

}
