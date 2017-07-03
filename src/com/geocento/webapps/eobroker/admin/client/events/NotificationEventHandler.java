package com.geocento.webapps.eobroker.admin.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by thomas on 30/05/2017.
 */
public interface NotificationEventHandler extends EventHandler {
    void onNotification(NotificationEvent event);
}
