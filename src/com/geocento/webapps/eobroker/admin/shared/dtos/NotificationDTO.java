package com.geocento.webapps.eobroker.admin.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.notifications.AdminNotification;

import java.util.Date;

/**
 * Created by thomas on 07/07/2016.
 */
public class NotificationDTO {

    AdminNotification.TYPE type;
    String message;
    private String linkId;
    private Date creationDate;

    public NotificationDTO() {
    }

    public AdminNotification.TYPE getType() {
        return type;
    }

    public void setType(AdminNotification.TYPE type) {
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
