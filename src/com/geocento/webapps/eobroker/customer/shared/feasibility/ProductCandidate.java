package com.geocento.webapps.eobroker.customer.shared.feasibility;

/**
 * Created by thomas on 27/07/2016.
 */
public class ProductCandidate {

    String geometryWKT;

    String name;
    String description;

    public ProductCandidate() {
    }

    public String getGeometryWKT() {
        return geometryWKT;
    }

    public void setGeometryWKT(String geometryWKT) {
        this.geometryWKT = geometryWKT;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
