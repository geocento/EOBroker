package com.geocento.webapps.eobroker.common.shared.entities;

/**
 * Created by thomas on 10/11/2016.
 */
public enum AccessType {
    webapplication("Web application"),
    download("Downloadable file"),
    wms("OGC WMS access"),
    wfs("OGC WFS access"),
    wcs("OGC WCS access"),
    api("API access");

    String name;

    AccessType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
