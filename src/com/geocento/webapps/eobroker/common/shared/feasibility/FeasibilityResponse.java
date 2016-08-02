package com.geocento.webapps.eobroker.common.shared.feasibility;

import org.geojson.object.FeatureCollection;

import java.util.List;

/**
 * Created by thomas on 12/07/2016.
 */
public class FeasibilityResponse {

    boolean feasible;
    String message;

    List<Feature> features;

    FeatureCollection coverages;

    String timeToDelivery;

    public FeasibilityResponse() {
    }

    public boolean isFeasible() {
        return feasible;
    }

    public void setFeasible(boolean feasible) {
        this.feasible = feasible;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public FeatureCollection getCoverages() {
        return coverages;
    }

    public void setCoverages(FeatureCollection coverages) {
        this.coverages = coverages;
    }

    public String getTimeToDelivery() {
        return timeToDelivery;
    }

    public void setTimeToDelivery(String timeToDelivery) {
        this.timeToDelivery = timeToDelivery;
    }
}
