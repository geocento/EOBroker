package com.geocento.webapps.eobroker.customer.shared.feasibility;

/**
 * Created by thomas on 02/06/2017.
 */
public class WMSStatistics extends Statistics {

    String baseUrl;
    String layerName;
    String version;
    String preferredStyle;

    public WMSStatistics() {
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPreferredStyle() {
        return preferredStyle;
    }

    public void setPreferredStyle(String preferredStyle) {
        this.preferredStyle = preferredStyle;
    }
}
