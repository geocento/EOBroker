package com.geocento.webapps.eobroker.supplier.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {

    public Resources INSTANCE =
            GWT.create(Resources.class);

    @Source("defaultStyle.xml")
    TextResource defaultStyle();
}