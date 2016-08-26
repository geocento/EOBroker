package com.geocento.webapps.eobroker.common.client.widgets.maps.resources;

/**
 * Created by thomas on 23/08/2016.
 */
public class WMSLayerInfoJSNI {

    protected WMSLayerInfoJSNI() {
    }

    public final native String getName() /*-{
        return this.name;
    }-*/;

    public final native void setName(String name) /*-{
        this.name = name;
    }-*/;

    public final native String getTitle() /*-{
        return this.title;
    }-*/;

    public final native void setTitle(String title) /*-{
        this.title = title;
    }-*/;

    static public final native WMSLayerInfoJSNI createInfo(String name, String title) /*-{
        return {
            name: name,
            title: title
        };
    }-*/;

}
