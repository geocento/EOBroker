package com.geocento.webapps.eobroker.supplier.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 15/06/2016.
 */
public class RemoveService extends GwtEvent<RemoveServiceHandler> {

    public static Type<RemoveServiceHandler> TYPE = new Type<RemoveServiceHandler>();

    private final Long serviceId;

    public RemoveService(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public Type<RemoveServiceHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveServiceHandler handler) {
        handler.onRemoveService(this);
    }
}
