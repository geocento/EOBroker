package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.NOTIFICATION_DELAY;

/**
 * Created by thomas on 13/03/2017.
 */
public class SupplierSettingsDTO {

    NOTIFICATION_DELAY notificationDelayMessages;
    NOTIFICATION_DELAY notificationDelayRequests;

    public SupplierSettingsDTO() {
    }

    public NOTIFICATION_DELAY getNotificationDelayMessages() {
        return notificationDelayMessages;
    }

    public void setNotificationDelayMessages(NOTIFICATION_DELAY notificationDelayMessages) {
        this.notificationDelayMessages = notificationDelayMessages;
    }

    public NOTIFICATION_DELAY getNotificationDelayRequests() {
        return notificationDelayRequests;
    }

    public void setNotificationDelayRequests(NOTIFICATION_DELAY notificationDelayRequests) {
        this.notificationDelayRequests = notificationDelayRequests;
    }
}
