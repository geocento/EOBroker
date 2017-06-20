package com.geocento.webapps.eobroker.common.shared.feasibility;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by thomas on 05/07/2016.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class FeasibilityResponse extends Response {

    FEASIBILITY feasible;
    String message;

    List<ProductCandidate> productCandidates;

    List<Statistics> statistics;

    String additionalComments;

    public FeasibilityResponse() {
    }

    /**
     *
     * whether the request is feasible
     *
     */
    public FEASIBILITY getFeasible() {
        return feasible;
    }

    public void setFeasible(FEASIBILITY feasible) {
        this.feasible = feasible;
    }

    /**
     *
     * comment on feasibility
     *
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * list of potential products that can be delivered
     *
     * @return
     */
    public List<ProductCandidate> getProductCandidates() {
        return productCandidates;
    }

    public void setProductCandidates(List<ProductCandidate> productCandidates) {
        this.productCandidates = productCandidates;
    }

    /**
     *
     * a generic array for any useful stats
     *
     * @return
     */
    public List<Statistics> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Statistics> statistics) {
        this.statistics = statistics;
    }

    /**
     *
     * any additional comments, will be displayed as HTML
     *
     * @return
     */
    public String getAdditionalComments() {
        return additionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        this.additionalComments = additionalComments;
    }
}
