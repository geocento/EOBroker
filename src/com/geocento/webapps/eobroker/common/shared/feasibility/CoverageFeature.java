package com.geocento.webapps.eobroker.common.shared.feasibility;

public class CoverageFeature {

    String wktValue;

    String name;
    String description;

    public CoverageFeature() {
    }

    public String getWktValue() {
        return wktValue;
    }

    public void setWktValue(String wktValue) {
        this.wktValue = wktValue;
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