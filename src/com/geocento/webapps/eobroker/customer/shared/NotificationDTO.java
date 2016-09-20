package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.notifications.Notification;

import java.util.Date;

/**
 * Created by thomas on 07/07/2016.
 */
public class NotificationDTO {

    Notification.TYPE type;
    String message;
    private String linkId;
    private Date creationDate;

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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
