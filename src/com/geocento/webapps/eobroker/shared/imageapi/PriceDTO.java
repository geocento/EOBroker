package com.geocento.webapps.eobroker.shared.imageapi;

/**
 * Created by thomas on 04/03/2016.
 */
public class PriceDTO {

    private String currency;
    private Double value;

    public PriceDTO() {
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }
}
