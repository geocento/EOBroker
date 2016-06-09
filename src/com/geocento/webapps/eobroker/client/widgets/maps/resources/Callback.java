package com.geocento.webapps.eobroker.client.widgets.maps.resources;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by thomas on 01/06/2016.
 */
public interface Callback<T extends JavaScriptObject> {
    void callback(T result);
}
