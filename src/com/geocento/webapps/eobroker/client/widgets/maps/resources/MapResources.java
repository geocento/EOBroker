package com.geocento.webapps.eobroker.client.widgets.maps.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Created by thomas on 01/06/2016.
 */
public interface MapResources extends ClientBundle {

    public MapResources INSTANCE =
            GWT.create(MapResources.class);

    @Source("arcgisMap.js")
    TextResource arcgisMap();

}
