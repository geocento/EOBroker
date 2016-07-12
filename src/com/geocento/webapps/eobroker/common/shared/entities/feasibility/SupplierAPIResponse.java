package com.geocento.webapps.eobroker.common.shared.entities.feasibility;

import java.util.List;

/**
 * Created by thomas on 05/07/2016.
 */
public class SupplierAPIResponse {

    boolean feasible;
    String message;

    List<Feature> features;

    public SupplierAPIResponse() {
    }

    public boolean isFeasible() {
        return feasible;
    }

    public void setFeasible(boolean feasible) {
        this.feasible = feasible;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }
}
