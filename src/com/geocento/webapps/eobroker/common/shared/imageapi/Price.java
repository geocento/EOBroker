package com.geocento.webapps.eobroker.common.shared.imageapi;

/**
 * a price structure
 */
public class Price {

    String currency;
    Double value;

    public Price() {
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     *
     * the currency using the ISO 4217 Currency Codes
     *
     * @return
     */
    public String getCurrency() {
        return currency;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    /**
     *
     * the price value in the specified currency
     *
     * @return
     */
    public Double getValue() {
        return value;
    }
}
