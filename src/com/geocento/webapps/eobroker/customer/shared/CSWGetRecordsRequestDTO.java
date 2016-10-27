package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.Extent;

import java.io.Serializable;

/**
 * Created by thomas on 10/10/2016.
 */
public class CSWGetRecordsRequestDTO implements Serializable {

    String uri;
    String text;
    Extent extent;

    public CSWGetRecordsRequestDTO() {
    }

    public CSWGetRecordsRequestDTO(String uri, String text, Extent extent) {
        this.uri = uri;
        this.text = text;
        this.extent = extent;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Extent getExtent() {
        return extent;
    }

    public void setExtent(Extent extent) {
        this.extent = extent;
    }
}
