package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;

/**
 * Created by thomas on 07/07/2016.
 */
public class SupplierNotificationDTO {

    SupplierNotification.TYPE type;
    String message;
    private String linkId;

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
}
