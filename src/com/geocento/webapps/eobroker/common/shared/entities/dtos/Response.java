package com.geocento.webapps.eobroker.common.shared.entities.dtos;

/**
 * Created by thomas on 11/07/2016.
 */
public class Response {
    int status;
    String error;

    public Response() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
