package com.geocento.webapps.eobroker.customer.client.events;

import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 13/06/2017.
 */
public class MessageEvent extends GwtEvent<MessageEventHandler> {

    public static Type<MessageEventHandler> TYPE = new Type<MessageEventHandler>();
    private MessageDTO message;

    public Type<MessageEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(MessageEventHandler handler) {
        handler.onMessage(this);
    }

    public void setMessage(MessageDTO message) {
        this.message = message;
    }

    public MessageDTO getMessage() {
        return message;
    }
}
