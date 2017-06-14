package com.geocento.webapps.eobroker.customer.client.events;

import com.geocento.webapps.eobroker.customer.shared.NotificationDTO;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 30/05/2017.
 */
public class NotificationEvent extends GwtEvent<NotificationEventHandler> {

    public static Type<NotificationEventHandler> TYPE = new Type<NotificationEventHandler>();

    private NotificationDTO notification;

    public Type<NotificationEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(NotificationEventHandler handler) {
        handler.onNotification(this);
    }

    public void setNotification(NotificationDTO notification) {
        this.notification = notification;
    }

    public NotificationDTO getNotification() {
        return notification;
    }
}
