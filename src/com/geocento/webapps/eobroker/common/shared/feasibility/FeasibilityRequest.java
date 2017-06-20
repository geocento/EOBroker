package com.geocento.webapps.eobroker.common.shared.feasibility;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 12/07/2016.
 */
public class FeasibilityRequest {

    String id;

    UserInformation userInformation;

    String aoiWKT;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    Date start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    Date stop;
    List<Parameter> serviceParameters;

    public FeasibilityRequest() {
    }

    /**
     *
     * ID of the request, generated on the broker side
     * the query can be retrieved using this ID
     *
     * @return
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * the user information from the user making the request
     *
     * @return
     */
    public UserInformation getUserInformation() {
        return userInformation;
    }

    public void setUserInformation(UserInformation userInformation) {
        this.userInformation = userInformation;
    }

    /**
     *
     * the AoI as a WKT string
     *
     * @return
     */
    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    /**
     *
     * start date using yyyy-MM-dd'T'HH:mm:ss.SSS'Z' format
     *
     * @return
     */
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    /**
     *
     * stop date using yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     *
     * @return
     */
    public Date getStop() {
        return stop;
    }

    public void setStop(Date stop) {
        this.stop = stop;
    }

    /**
     *
     * product category service specific parameters
     *m
     * @return
     */
    public List<Parameter> getServiceParameters() {
        return serviceParameters;
    }

    public void setServiceParameters(List<Parameter> serviceParameters) {
        this.serviceParameters = serviceParameters;
    }
}
