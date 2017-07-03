package com.geocento.webapps.eobroker.supplier.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 28/06/2017.
 */
public class WebSocketClosedEvent extends GwtEvent<WebSocketClosedEventHandler> {
    public static Type<WebSocketClosedEventHandler> TYPE = new Type<WebSocketClosedEventHandler>();

    public Type<WebSocketClosedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(WebSocketClosedEventHandler handler) {
        handler.onWebSocketClosed(this);
    }
}
