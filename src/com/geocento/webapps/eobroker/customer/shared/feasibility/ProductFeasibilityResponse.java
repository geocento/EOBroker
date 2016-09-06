package com.geocento.webapps.eobroker.customer.shared.feasibility;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by thomas on 05/07/2016.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class ProductFeasibilityResponse {

    String id;

    // the feasibility part
    public enum FEASIBILITY {NONE, PARTIAL, GOOD};
    // whether the request is feasible
    FEASIBILITY feasible;
    // comment on feasibility
    String message;

    // the coverage part
    List<Sensor> sensors;
    // coverage geometries
    List<CoverageFeature> coverages;
    // the coverage dynamics
    List<TimeValue> coverageTime;
    // average coverage
    double bestCoverageValue;

    // TODO - add optional footprints?

    // the revisit part
    Integer bestRevisitRate;
    boolean siteMonitoringAvailable;

    // features detected
    List<Feature> features;

    // sample outputs
    Samples samples;

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

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public List<TimeValue> getCoverageTime() {
        return coverageTime;
    }

    public void setCoverageTime(List<TimeValue> coverageTime) {
        this.coverageTime = coverageTime;
    }

    public Integer getBestRevisitRate() {
        return bestRevisitRate;
    }

    public void setBestRevisitRate(Integer bestRevisitRate) {
        this.bestRevisitRate = bestRevisitRate;
    }

    public boolean isSiteMonitoringAvailable() {
        return siteMonitoringAvailable;
    }

    public void setSiteMonitoringAvailable(boolean siteMonitoringAvailable) {
        this.siteMonitoringAvailable = siteMonitoringAvailable;
    }

    public void setBestCoverageValue(double bestCoverageValue) {
        this.bestCoverageValue = bestCoverageValue;
    }

    public double getBestCoverageValue() {
        return bestCoverageValue;
    }

    public Samples getSamples() {
        return samples;
    }

    public void setSamples(Samples samples) {
        this.samples = samples;
    }
}
