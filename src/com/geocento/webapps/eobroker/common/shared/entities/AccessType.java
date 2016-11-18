package com.geocento.webapps.eobroker.common.shared.entities;

/**
 * Created by thomas on 10/11/2016.
 */
public enum AccessType {
    application("Web application"),
    file("Downloadable file"),
    ogc("OGC OWS services"),
    api("API access");

    String name;

    AccessType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
