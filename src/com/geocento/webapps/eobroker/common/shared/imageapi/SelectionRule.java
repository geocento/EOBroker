package com.geocento.webapps.eobroker.common.shared.imageapi;

/**
 * Created by thomas on 04/03/2016.
 */
public class SelectionRule {

    SensorFilters sensorFilters;
    ProductFilters productFilters;

    public SelectionRule() {
    }

    public SensorFilters getSensorFilters() {
        return sensorFilters;
    }

    public void setSensorFilters(SensorFilters sensorFilters) {
        this.sensorFilters = sensorFilters;
    }

    public ProductFilters getProductFilters() {
        return productFilters;
    }

    public void setProductFilters(ProductFilters productFilters) {
        this.productFilters = productFilters;
    }
}
