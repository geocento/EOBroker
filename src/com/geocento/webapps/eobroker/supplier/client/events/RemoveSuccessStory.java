package com.geocento.webapps.eobroker.supplier.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 20/03/2017.
 */
public class RemoveSuccessStory extends GwtEvent<RemoveSuccessStoryHandler> {

    public static Type<RemoveSuccessStoryHandler> TYPE = new Type<RemoveSuccessStoryHandler>();

    private final Long id;

    public RemoveSuccessStory(Long id) {
        this.id = id;
    }

    public Type<RemoveSuccessStoryHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveSuccessStoryHandler handler) {
        handler.onRemoveSuccessStory(this);
    }
}
