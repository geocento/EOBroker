package com.geocento.webapps.eobroker.common.shared.entities;

/**
 * Created by thomas on 11/11/2016.
 */
public enum ServiceType {
    free("Free"), commercial("Commercial");

    private final String name;

    ServiceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
