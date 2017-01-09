package com.geocento.webapps.eobroker.common.shared.entities;

import gwt.material.design.client.constants.IconType;

import javax.persistence.*;

/**
 * Created by thomas on 10/11/2016.
 */
@Entity
@DiscriminatorValue("OGC")
public class DatasetAccessOGC extends DatasetAccess {

    @Column(length = 1000)
    String serverUrl;
    private String styleName;

    public DatasetAccessOGC() {
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }


    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }
}
