package com.geocento.webapps.eobroker.customer.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by thomas on 13/06/2017.
 */
public interface MessageEventHandler extends EventHandler {
    void onMessage(MessageEvent event);
}
