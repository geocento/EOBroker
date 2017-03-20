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

    Double minValue;
    Double maxValue;

    @Column
    String comment;

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

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
