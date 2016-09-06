package com.geocento.webapps.eobroker.customer.client.events;

import com.geocento.webapps.eobroker.customer.shared.RequestDTO;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 29/08/2016.
 */
public class RequestCreated extends GwtEvent<ImageOrderCreatedHandler> {

    public static Type<ImageOrderCreatedHandler> TYPE = new Type<ImageOrderCreatedHandler>();

    private final RequestDTO imageOrder;

    public RequestCreated(RequestDTO imageOrder) {
        this.imageOrder = imageOrder;
    }

    public RequestDTO getImageOrder() {
        return imageOrder;
    }

    public Type<ImageOrderCreatedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ImageOrderCreatedHandler handler) {
        handler.onImageOrderCreated(this);
    }
}
