package com.geocento.webapps.eobroker.common.shared.imageapi;

/**
 * NOT SUPPORTED YET - the response returned by a create alert query
 */
public class CreateAlertResponse extends StatusResponse {

    String id;

    public CreateAlertResponse() {
    }

    /**
     *
     * the id of the created image alert
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
