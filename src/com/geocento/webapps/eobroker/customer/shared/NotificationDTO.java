package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.notifications.Notification;

/**
 * Created by thomas on 07/07/2016.
 */
public class NotificationDTO {

    Notification.TYPE type;
    String message;
    private String linkId;

    public NotificationDTO() {
    }

    public Notification.TYPE getType() {
        return type;
    }

    public void setType(Notification.TYPE type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }
}
