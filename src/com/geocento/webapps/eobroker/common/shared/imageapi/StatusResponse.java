package com.geocento.webapps.eobroker.common.shared.imageapi;

/**
 *
 * a standard status response
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

    /**
     *
     * the error code
     *
     * @return
     */
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     *
     * an error message
     *
     * @return
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
