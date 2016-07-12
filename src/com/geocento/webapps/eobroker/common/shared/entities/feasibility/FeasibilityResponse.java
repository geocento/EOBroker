package com.geocento.webapps.eobroker.common.shared.entities.feasibility;

import java.util.List;

/**
 * Created by thomas on 12/07/2016.
 */
public class FeasibilityResponse {

    boolean feasible;
    String message;

    List<Feature> features;

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
}
