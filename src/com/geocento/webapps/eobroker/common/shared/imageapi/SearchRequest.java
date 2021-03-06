package com.geocento.webapps.eobroker.common.shared.imageapi;

/**
 *
 * Represents a search request with the various search fields allowed
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

    /**
     *
     * a wildcard type of sensor selection based on the name of the satellite and instrument
     * For instance:
     * - senti will match all satellites which name starts with 'senti'
     * specify either sensors or sensorFilters but not both
     *
     * @return
     */
    public String getSensors() {
        return sensors;
    }

    public void setSensors(String sensors) {
        this.sensors = sensors;
    }

    /**
     *
     * a sensor filter, allows to select sensors using criteria. See {@link SensorFilters} to see the list of criteria supported
     * specify either sensors or sensorFilters but not both
     *
     * @return
     */
    public SensorFilters getSensorFilters() {
        return sensorFilters;
    }

    public void setSensorFilters(SensorFilters sensorFilters) {
        this.sensorFilters = sensorFilters;
    }

    /**
     *
     * the minimum date for the acquisition start date expressed as a Unix timestamp
     *
     * @return
     */
    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    /**
     *
     * the maximum date for the acquisition start date expressed as a Unix timestamp
     *
     * @return
     */
    public Long getStop() {
        return stop;
    }

    public void setStop(Long stop) {
        this.stop = stop;
    }

    /**
     *
     * the area of interest for the search expressed using WKT
     * Only simple non intersecting geometries are supported, MULTI are not supported, run several queries instead
     *
     * @return
     */
    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    /**
     *
     * the search constraints to apply, see {@link ProductFilters}
     *
     * @return
     */
    public ProductFilters getFilters() {
        return filters;
    }

    public void setFilters(ProductFilters filters) {
        this.filters = filters;
    }

    /**
     *
     * the maximum number of results to return
     * the EarthImages API does not provide pagination
     *
     * @return
     */
    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     *
     * the currency to use when providing prices
     *
     * @return
     */
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
