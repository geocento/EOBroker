package com.geocento.webapps.eobroker.customer.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 16/06/2016.
 */
public class RequestImagery extends GwtEvent<RequestImageryHandler> {
    public static Type<RequestImageryHandler> TYPE = new Type<RequestImageryHandler>();

    public Type<RequestImageryHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RequestImageryHandler handler) {
        handler.onRequestImagery(this);
    }
}
