package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.feasibility.FeasibilityResponse;

/**
 * Created by thomas on 05/07/2016.
 */
public class FeasibilityResponseDTO {

    String searchId;
    FeasibilityResponse feasibilityResponse;

    public FeasibilityResponseDTO() {
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public FeasibilityResponse getFeasibilityResponse() {
        return feasibilityResponse;
    }

    public void setFeasibilityResponse(FeasibilityResponse feasibilityResponse) {
        this.feasibilityResponse = feasibilityResponse;
    }
}
