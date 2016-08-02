package com.geocento.webapps.eobroker.common.shared.imageapi;

/**
 * NOT SUPPORTED YET - an ordering parameter
 */
public class OrderParameter {

    Long id;
    String name;
    String value;
    Price price;

    public OrderParameter() {
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

    public void setPrice(Price price) {
        this.price = price;
    }

    public Price getPrice() {
        return price;
    }
}
