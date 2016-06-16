package com.geocento.webapps.eobroker.customer.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by thomas on 16/06/2016.
 */
public interface RequestImageryHandler extends EventHandler {
    void onRequestImagery(RequestImagery event);
}
