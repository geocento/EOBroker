package com.geocento.webapps.eobroker.supplier.client.places;

import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierNotificationDTO;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 14/06/2017.
 */
public class NotificationEvent extends GwtEvent<NotificationEventHandler> {

    public static Type<NotificationEventHandler> TYPE = new Type<NotificationEventHandler>();

    private SupplierNotificationDTO supplierNotificationDTO;

    public NotificationEvent(SupplierNotificationDTO supplierNotificationDTO) {
        this.supplierNotificationDTO = supplierNotificationDTO;
    }

    public SupplierNotificationDTO getSupplierNotificationDTO() {
        return supplierNotificationDTO;
    }

    public Type<NotificationEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(NotificationEventHandler handler) {
        handler.onNotification(this);
    }
}
