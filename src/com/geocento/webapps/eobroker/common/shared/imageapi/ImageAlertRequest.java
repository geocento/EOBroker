package com.geocento.webapps.eobroker.common.shared.imageapi;

/**
 *
 * Request object to create an image alert on the EarthImages server
 * Image alerts enables an account to be notified of new imagery acquired matching a search request
 * The parameters are the same as for {@link SearchRequest}
 * Notifications are sent to the account notification URL
 */
public class ImageAlertRequest {

    String sensors;
    SensorFilters sensorFilters;
    Long start;
    Long stop;
    String aoiWKT;
    ProductFilters filters;
    Integer limit = 100;
    String currency = "EUR";

    public ImageAlertRequest() {
    }

    /**
     *
     * as in {@link SearchRequest}
     *
     * @return sensors
     */
    public String getSensors() {
        return sensors;
    }

    public void setSensors(String sensors) {
        this.sensors = sensors;
    }

    /**
     *
     * as in {@link SearchRequest}
     *
     * @return sensorFilters
     */
    public SensorFilters getSensorFilters() {
        return sensorFilters;
    }

    public void setSensorFilters(SensorFilters sensorFilters) {
        this.sensorFilters = sensorFilters;
    }

    /**
     *
     * as in {@link SearchRequest}
     *
     * @return start
     */
    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    /**
     *
     * as in {@link SearchRequest}
     *
     * @return stop
     */
    public Long getStop() {
        return stop;
    }

    public void setStop(Long stop) {
        this.stop = stop;
    }

    /**
     *
     * as in {@link SearchRequest}
     *
     * @return aoiWKT
     */
    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    /**
     *
     * as in {@link SearchRequest}
     *
     * @return filters
     */
    public ProductFilters getFilters() {
        return filters;
    }

    public void setFilters(ProductFilters filters) {
        this.filters = filters;
    }

    /**
     *
     * as in {@link SearchRequest}
     *
     * @return limit
     */
    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     *
     * as in {@link SearchRequest}
     *
     * @return currency
     */
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
