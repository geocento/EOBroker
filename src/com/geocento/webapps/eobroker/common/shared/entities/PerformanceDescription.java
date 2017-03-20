package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;

/**
 * Created by thomas on 10/11/2016.
 */
@Entity
public class PerformanceDescription {

    @Id
    @GeneratedValue
    Long id;

    @Column(length = 100)
    String name;

    @Column(length = 1000)
    String description;

    @Column(length = 100)
    String unit;

    public PerformanceDescription() {
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
