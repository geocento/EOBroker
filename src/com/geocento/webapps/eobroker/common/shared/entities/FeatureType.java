package com.geocento.webapps.eobroker.common.shared.entities;

public enum FeatureType {

    UNSPECFIED("Unspecified"), BOOLEAN("Boolean"), NUMBER("Number"), TEXT("Text"), IMAGE("Image"), MAPVECTOR("Geospatial vector");

    String name;

    FeatureType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
};
    
