package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;

/**
 * Created by thomas on 10/11/2016.
 */
@Entity
@DiscriminatorValue("OGC")
public class DatasetAccessOGC extends DatasetAccess {

    @Column(length = 1000)
    String serverUrl;

    public DatasetAccessOGC() {
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
