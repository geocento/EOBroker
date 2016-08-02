package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.feasibility.CoverageFeature;
import com.geocento.webapps.eobroker.common.shared.feasibility.Feature;

import java.util.List;

/**
 * Created by thomas on 05/07/2016.
 */
public class SupplierAPIResponse {

    boolean feasible;
    String message;

    List<Feature> features;

    List<CoverageFeature> coverages;

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

    public List<CoverageFeature> getCoverages() {
        return coverages;
    }

    public void setCoverages(List<CoverageFeature> coverages) {
        this.coverages = coverages;
    }
}
