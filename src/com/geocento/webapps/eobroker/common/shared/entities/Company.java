package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity
public class Company {

    @Id
    @GeneratedValue
    Long id;

    @Column(length = 1000, unique = true)
    String name;

    @Column(length = 1000)
    String iconURL;

    @Column(length = 1000)
    String description;

    public Company() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
