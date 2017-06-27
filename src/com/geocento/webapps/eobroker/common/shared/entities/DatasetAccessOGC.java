package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by thomas on 10/11/2016.
 */
@Entity
@DiscriminatorValue("OGC")
public class DatasetAccessOGC extends DatasetAccess {

    @Column(length = 1000)
    String serverUrl;
    String styleName;

    @Column(length = 1000)
    String wcsServerUrl;
    String wcsResourceName;

    boolean corsEnabled;

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

    public String getWcsServerUrl() {
        return wcsServerUrl;
    }

    public void setWcsServerUrl(String wcsServerUrl) {
        this.wcsServerUrl = wcsServerUrl;
    }

    public String getWcsResourceName() {
        return wcsResourceName;
    }

    public void setWcsResourceName(String wcsResourceName) {
        this.wcsResourceName = wcsResourceName;
    }

    public boolean isCorsEnabled() {
        return corsEnabled;
    }

    public void setCorsEnabled(boolean corsEnabled) {
        this.corsEnabled = corsEnabled;
    }
}
