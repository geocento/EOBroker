package com.geocento.webapps.eobroker.common.shared.imageapi;

/**
 * Created by thomas on 07/03/2016.
 */
public class SearchRequest {

    String sensors;
    SensorFilters sensorFilters;
    Long start;
    Long stop;
    String aoiWKT;
    ProductFilters filters;
    Integer limit = 100;
    String currency = "EUR";

    public SearchRequest() {
    }

    public String getSensors() {
        return sensors;
    }

    public void setSensors(String sensors) {
        this.sensors = sensors;
    }

    public SensorFilters getSensorFilters() {
        return sensorFilters;
    }

    public void setSensorFilters(SensorFilters sensorFilters) {
        this.sensorFilters = sensorFilters;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getStop() {
        return stop;
    }

    public void setStop(Long stop) {
        this.stop = stop;
    }

    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    public ProductFilters getFilters() {
        return filters;
    }

    public void setFilters(ProductFilters filters) {
        this.filters = filters;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
