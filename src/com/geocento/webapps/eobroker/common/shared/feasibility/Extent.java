package com.geocento.webapps.eobroker.common.shared.feasibility;

import java.io.Serializable;

/**
 * Created by thomas on 07/10/2016.
 */
public class Extent implements Serializable {

    double south;
    double west;
    double north;
    double east;

    public Extent() {
    }

    public double getSouth() {
        return south;
    }

    public void setSouth(double south) {
        this.south = south;
    }

    public double getWest() {
        return west;
    }

    public void setWest(double west) {
        this.west = west;
    }

    public double getNorth() {
        return north;
    }

    public void setNorth(double north) {
        this.north = north;
    }

    public double getEast() {
        return east;
    }

    public void setEast(double east) {
        this.east = east;
    }
}
