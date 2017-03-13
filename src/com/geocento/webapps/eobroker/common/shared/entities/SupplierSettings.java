package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;

/**
 * Created by thomas on 13/03/2017.
 */
@Entity
public class SupplierSettings {

    @Id
    @GeneratedValue
    Long id;

    @OneToOne(mappedBy = "settings")
    Company company;

    @Enumerated(EnumType.STRING)
    NOTIFICATION_DELAY notificationDelayMessages;

    @Enumerated(EnumType.STRING)
    NOTIFICATION_DELAY notificationDelayRequests;

    public SupplierSettings() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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
