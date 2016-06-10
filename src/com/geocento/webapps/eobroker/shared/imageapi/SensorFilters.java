package com.geocento.webapps.eobroker.shared.imageapi;

import java.util.Set;

/**
 * Created by thomas on 03/03/2016.
 */
public class SensorFilters {

    Double minResolution;
    Double maxResolution;

    String type;

    String opticalBands;

    String[] radarBands;

    boolean includeFutureMissions = false;
    boolean orderableOnly = false;
    Set<String> suppliers;

    public SensorFilters() {
    }

    public double getMinResolution() {
        return minResolution;
    }

    public void setMinResolution(double minResolution) {
        this.minResolution = minResolution;
    }

    public double getMaxResolution() {
        return maxResolution;
    }

    public void setMaxResolution(double maxResolution) {
        this.maxResolution = maxResolution;
    }

    public boolean isIncludeFutureMissions() {
        return includeFutureMissions;
    }

    public void setIncludeFutureMissions(boolean includeFutureMissions) {
        this.includeFutureMissions = includeFutureMissions;
    }

    public boolean isOrderableOnly() {
        return orderableOnly;
    }

    public void setOrderableOnly(boolean orderableOnly) {
        this.orderableOnly = orderableOnly;
    }

    public Set<String> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(Set<String> suppliers) {
        this.suppliers = suppliers;
    }

}
