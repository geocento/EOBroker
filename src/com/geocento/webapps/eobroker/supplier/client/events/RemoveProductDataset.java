package com.geocento.webapps.eobroker.supplier.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 03/11/2016.
 */
public class RemoveProductDataset extends GwtEvent<RemoveProductDatasetHandler> {

    public static Type<RemoveProductDatasetHandler> TYPE = new Type<RemoveProductDatasetHandler>();

    private final Long id;

    public RemoveProductDataset(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Type<RemoveProductDatasetHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveProductDatasetHandler handler) {
        handler.onRemoveProductDataset(this);
    }
}
