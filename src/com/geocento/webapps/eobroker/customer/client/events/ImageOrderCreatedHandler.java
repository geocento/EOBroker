package com.geocento.webapps.eobroker.customer.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by thomas on 29/08/2016.
 */
public interface ImageOrderCreatedHandler extends EventHandler {
    void onImageOrderCreated(RequestCreated event);
}
