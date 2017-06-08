package com.geocento.webapps.eobroker.customer.shared.feasibility;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by thomas on 05/07/2016.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class ProductFeasibilityResponse {

    String id;

    // whether the request is feasible
    FEASIBILITY feasible;
    // comment on feasibility
    String message;

    // coverage information, ie the products that can be delivered
    List<CoverageFeature> coverages;

    // the information and stats
    List<DataSource> dataSources;
    // features detected
    List<Feature> features;
    // stats, a generic array for any useful stats
    List<Statistics> statistics;

    public ProductFeasibilityResponse() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FEASIBILITY getFeasible() {
        return feasible;
    }

    public void setFeasible(FEASIBILITY feasible) {
        this.feasible = feasible;
    }

    public List<DataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public List<Statistics> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Statistics> statistics) {
        this.statistics = statistics;
    }
}
