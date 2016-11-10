package com.geocento.webapps.eobroker.supplier.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 08/11/2016.
 */
public class RemoveSoftware extends GwtEvent<RemoveSoftwareHandler> {

    public static Type<RemoveSoftwareHandler> TYPE = new Type<RemoveSoftwareHandler>();

    private final Long softwareId;

    public RemoveSoftware(Long id) {
        this.softwareId = id;
    }

    public Long getSoftwareId() {
        return softwareId;
    }

    public Type<RemoveSoftwareHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveSoftwareHandler handler) {
        handler.onRemoveSoftware(this);
    }
}
