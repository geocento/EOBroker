package com.geocento.webapps.eobroker.supplier.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 07/10/2016.
 */
public class RemoveDataset extends GwtEvent<RemoveDatasetHandler> {

    public static Type<RemoveDatasetHandler> TYPE = new Type<RemoveDatasetHandler>();

    private final Long id;

    public RemoveDataset(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Type<RemoveDatasetHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveDatasetHandler handler) {
        handler.onRemoveDataset(this);
    }
}
