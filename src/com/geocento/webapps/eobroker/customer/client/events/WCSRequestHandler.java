package com.geocento.webapps.eobroker.customer.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by thomas on 14/06/2017.
 */
public interface WCSRequestHandler extends EventHandler {
    void onWCSRequest(WCSRequest event);
}
