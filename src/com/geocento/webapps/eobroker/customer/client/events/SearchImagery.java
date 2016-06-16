package com.geocento.webapps.eobroker.customer.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 16/06/2016.
 */
public class SearchImagery extends GwtEvent<SearchImageryHandler> {

    public static Type<SearchImageryHandler> TYPE = new Type<SearchImageryHandler>();

    public Type<SearchImageryHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(SearchImageryHandler handler) {
        handler.onSearchImagery(this);
    }
}
