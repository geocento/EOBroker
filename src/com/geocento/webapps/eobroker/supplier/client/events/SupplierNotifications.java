package com.geocento.webapps.eobroker.supplier.client.events;

import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierNotificationDTO;
import com.google.gwt.event.shared.GwtEvent;

import java.util.List;

/**
 * Created by thomas on 07/07/2016.
 */
public class SupplierNotifications extends GwtEvent<SupplierNotificationsHandler> {

    public static Type<SupplierNotificationsHandler> TYPE = new Type<SupplierNotificationsHandler>();

    private final List<SupplierNotificationDTO> supplierNotifications;

    public SupplierNotifications(List<SupplierNotificationDTO> supplierNotifications) {
        this.supplierNotifications = supplierNotifications;
    }

    public List<SupplierNotificationDTO> getSupplierNotifications() {
        return supplierNotifications;
    }

    public Type<SupplierNotificationsHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(SupplierNotificationsHandler handler) {
        handler.onSupplierNotifications(this);
    }
}
