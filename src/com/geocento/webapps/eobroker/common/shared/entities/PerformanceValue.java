package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;

/**
 * Created by thomas on 10/11/2016.
 */
@Entity
public class PerformanceValue {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    PerformanceDescription performanceDescription;

    @Column(length = 1000)
    Double value;

    public PerformanceValue() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PerformanceDescription getPerformanceDescription() {
        return performanceDescription;
    }

    public void setPerformanceDescription(PerformanceDescription performanceDescription) {
        this.performanceDescription = performanceDescription;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
