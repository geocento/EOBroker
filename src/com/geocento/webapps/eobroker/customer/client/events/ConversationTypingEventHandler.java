package com.geocento.webapps.eobroker.customer.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by thomas on 04/12/2017.
 */
public interface ConversationTypingEventHandler extends EventHandler {
    void onConversationTyping(ConversationTypingEvent event);
}
