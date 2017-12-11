package com.geocento.webapps.eobroker.customer.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 04/12/2017.
 */
public class ConversationTypingEvent extends GwtEvent<ConversationTypingEventHandler> {

    public static Type<ConversationTypingEventHandler> TYPE = new Type<ConversationTypingEventHandler>();

    private String destination;

    public Type<ConversationTypingEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ConversationTypingEventHandler handler) {
        handler.onConversationTyping(this);
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }
}
