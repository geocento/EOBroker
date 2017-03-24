package com.geocento.webapps.eobroker.common.shared.entities;

/**
 * Created by thomas on 24/03/2017.
 */
public enum SoftwareType {
    free("Free"), commercial("Commercial");

    private final String name;

    SoftwareType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
