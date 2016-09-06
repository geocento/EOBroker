package com.geocento.webapps.eobroker.customer.shared.feasibility;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Sensor {

    public enum SENSORTYPE {mannedaircraft, drone, satellite};
    public enum IMAGETYPE {optical, radar, signal};

    SENSORTYPE sensortype;
    IMAGETYPE imagetype;
    String name;
    String description;

    public Sensor() {
    }

    public Sensor(SENSORTYPE sensortype, IMAGETYPE imagetype, String name, String description) {
        this.sensortype = sensortype;
        this.imagetype = imagetype;
        this.name = name;
        this.description = description;
    }

    public SENSORTYPE getSensortype() {
        return sensortype;
    }

    public void setSensortype(SENSORTYPE sensortype) {
        this.sensortype = sensortype;
    }

    public IMAGETYPE getImagetype() {
        return imagetype;
    }

    public void setImagetype(IMAGETYPE imagetype) {
        this.imagetype = imagetype;
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

