package com.geocento.webapps.eobroker.customer.client.styles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by thomas on 26/09/2014.
 */
public interface StyleResources extends ClientBundle {

    public StyleResources INSTANCE =
            GWT.create(StyleResources.class);

    public interface Style extends CssResource {

        String highlighted();

        String searchResult();
    }

    @Source({"Style.css"})
    Style style();

    @Source("images/eobe-logo.jpg")
    ImageResource logoEOBroker();

    @Source("images/informationIcon.png")
    ImageResource info();
}
