package com.geocento.webapps.eobroker.admin.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 26/10/2016.
 */
public class RemoveProduct extends GwtEvent<RemoveProductHandler> {

    public static Type<RemoveProductHandler> TYPE = new Type<RemoveProductHandler>();

    private Long id;

    public RemoveProduct(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Type<RemoveProductHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveProductHandler handler) {
        handler.onRemoveProduct(this);
    }
}
