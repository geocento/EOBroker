package com.geocento.webapps.eobroker.common.shared.feasibility;

/**
 * Created by thomas on 15/06/2017.
 */
public class Parameter {

    String name;
    Object value;

    public Parameter() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
