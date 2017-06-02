package com.geocento.webapps.eobroker.common.shared.feasibility;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 12/07/2016.
 */
public class FeasibilityRequest {

    String aoiWKT;
    Date start;
    Date stop;
    List<Parameter> parameters;

    public FeasibilityRequest() {
    }

    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getStop() {
        return stop;
    }

    public void setStop(Date stop) {
        this.stop = stop;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}
