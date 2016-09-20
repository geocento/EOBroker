package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;

import java.util.Date;

/**
 * Created by thomas on 07/07/2016.
 */
public class SupplierNotificationDTO {

    SupplierNotification.TYPE type;
    String message;
    private String linkId;
    private Date creationDate;

    public SupplierNotificationDTO() {
    }

    public SupplierNotification.TYPE getType() {
        return type;
    }

    public void setType(SupplierNotification.TYPE type) {
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

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}
