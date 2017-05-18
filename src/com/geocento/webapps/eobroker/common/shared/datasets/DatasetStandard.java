package com.geocento.webapps.eobroker.common.shared.datasets;

/**
 * Created by thomas on 16/05/2017.
 */
public enum DatasetStandard {

    CSW("Catalog Web Service"), OpenSearch("Open Search"), ogcWFS("OGC WFS");

    private final String name;

    DatasetStandard(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
