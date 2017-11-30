package com.geocento.webapps.eobroker.customer.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by thomas on 30/11/2017.
 */
public interface ConversationEventHandler extends EventHandler {
    void onConversation(ConversationEvent event);
}
