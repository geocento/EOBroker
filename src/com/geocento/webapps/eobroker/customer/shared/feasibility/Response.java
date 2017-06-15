package com.geocento.webapps.eobroker.customer.shared.feasibility;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by thomas on 15/06/2017.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Response {

    String id;
    int statusCode;
    String statusMessage;

    public Response() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
