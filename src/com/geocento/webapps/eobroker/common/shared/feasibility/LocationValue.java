package com.geocento.webapps.eobroker.common.shared.feasibility;

/**
 * Created by thomas on 15/06/2017.
 */
public class LocationValue {

    Position position;
    Double value;

    public LocationValue() {
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
