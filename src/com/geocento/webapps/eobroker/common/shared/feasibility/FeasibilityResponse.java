package com.geocento.webapps.eobroker.common.shared.feasibility;

import java.util.List;

/**
 * Created by thomas on 12/07/2016.
 */
public class FeasibilityResponse {

    STATUS feasible;
    String message;

    List<CoverageFeature> features;

    List<Statistics> statistics;

    String additionalComments;

    public FeasibilityResponse() {
    }

    public STATUS getFeasible() {
        return feasible;
    }

    public void setFeasible(STATUS feasible) {
        this.feasible = feasible;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CoverageFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<CoverageFeature> features) {
        this.features = features;
    }

    public List<Statistics> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Statistics> statistics) {
        this.statistics = statistics;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }
}
