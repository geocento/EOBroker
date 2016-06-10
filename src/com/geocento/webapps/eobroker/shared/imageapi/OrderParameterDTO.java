package com.geocento.webapps.eobroker.shared.imageapi;

/**
 * Created by thomas on 03/03/2016.
 */
public class OrderParameterDTO {

    Long id;
    String name;
    String value;

    public OrderParameterDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
