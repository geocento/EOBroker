package com.geocento.webapps.eobroker.common.shared.entities;

/**
 * Created by thomas on 06/06/2016.
 */
public enum Thematic {

    land("Land"),
    builtenvironment("Built environment"),
    marine("Marine"),
    geohazards("Geohazards"),
    atomsphereandclimate("Atmosphere & climate");

    private final String name;

    private Thematic(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

    public static Thematic fromName(String value) {
        for(Thematic thematic : values()) {
            if(thematic.equalsName(value)) {
                return thematic;
            }
        }
        return null;
    }
}
