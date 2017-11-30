package com.geocento.webapps.eobroker.customer.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 30/11/2017.
 */
public class ConversationEvent extends GwtEvent<ConversationEventHandler> {
    public static Type<ConversationEventHandler> TYPE = new Type<ConversationEventHandler>();
    private boolean online;
    private String destination;

    public Type<ConversationEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ConversationEventHandler handler) {
        handler.onConversation(this);
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isOnline() {
        return online;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }
}
