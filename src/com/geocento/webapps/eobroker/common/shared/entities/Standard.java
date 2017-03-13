package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by thomas on 10/03/2017.
 */
@Entity
public class Standard {

    @Id
    @GeneratedValue
    Long id;

    @Column(length = 100, unique = true)
    String name;

    @Column(length = 1000)
    String description;

    public Standard() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
