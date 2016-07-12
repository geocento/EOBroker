package com.geocento.webapps.eobroker.common.shared.entities.feasibility;

/**
 * Created by thomas on 12/07/2016.
 */
public class Feature {

    static public enum TYPE {BOOLEAN, NUMBER, TEXT, IMAGE, MAPVECTOR};

    TYPE type;
    String name;
    String description;

    public Feature() {
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

    public void setDescription(String description) {
        this.description = description;
    }
}
