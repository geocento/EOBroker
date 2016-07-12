package com.geocento.webapps.eobroker.common.shared.entities.notifications;

import com.geocento.webapps.eobroker.common.shared.entities.Company;

import javax.persistence.*;

/**
 * Created by thomas on 06/07/2016.
 */
@Entity
public class SupplierNotification {

    static public enum TYPE {MESSAGE, REQUEST, ORDER};

    @Id
    @GeneratedValue
    Long id;

    @Enumerated(EnumType.STRING)
    TYPE type;

    @Column(length = 1000)
    String message;

    @ManyToOne
    Company company;

    @Column(length = 100)
    String linkId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String text) {
        this.message = text;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

}
