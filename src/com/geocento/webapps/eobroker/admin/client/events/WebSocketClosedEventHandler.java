package com.geocento.webapps.eobroker.admin.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by thomas on 28/06/2017.
 */
public interface WebSocketClosedEventHandler extends EventHandler {
    void onWebSocketClosed(WebSocketClosedEvent event);
}
