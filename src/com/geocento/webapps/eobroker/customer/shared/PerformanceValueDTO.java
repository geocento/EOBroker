package com.geocento.webapps.eobroker.customer.shared;

/**
 * Created by thomas on 29/11/2017.
 */
public class PerformanceValueDTO {

    Long id;
    Double minValue;
    Double maxValue;

    public PerformanceValueDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
