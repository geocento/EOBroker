package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by thomas on 14/06/2017.
 */
public interface NotificationEventHandler extends EventHandler {
    void onNotification(NotificationEvent event);
}
