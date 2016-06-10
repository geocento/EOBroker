package com.geocento.webapps.eobroker.shared.imageapi;

/**
 * Created by thomas on 07/03/2016.
 */
public class StatusResponse {

    int errorCode;
    String errorMessage;

    public StatusResponse() {
    }

    public StatusResponse(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
