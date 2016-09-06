package com.geocento.webapps.eobroker.common.shared.feasibility;

/**
 * Created by thomas on 12/07/2016.
 */
public class Feature {

    static public enum TYPE {BOOLEAN, NUMBER, TEXT, IMAGE, MAPVECTOR};
    static public enum AVAILABILITY {NOTAVAILABLE, PARTIAL, AVAILABLE};

    TYPE type;
    String name;
    String description;
    AVAILABILITY availability;

    public Feature() {
    }

    public Feature(TYPE type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
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

    public AVAILABILITY getAvailability() {
        return availability;
    }

    public void setAvailability(AVAILABILITY availability) {
        this.availability = availability;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
