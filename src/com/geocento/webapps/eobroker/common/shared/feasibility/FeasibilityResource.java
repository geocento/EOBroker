package com.geocento.webapps.eobroker.common.shared.feasibility;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by thomas on 12/07/2016.
 */
@Path("/feasibility")
public class FeasibilityResource {

    @POST
    @Path("/exampleservice")
    @Produces("application/json")
    public FeasibilityResponse checkFeasibilityOilSpill(FeasibilityRequest request) {
        return null;
    }

}
