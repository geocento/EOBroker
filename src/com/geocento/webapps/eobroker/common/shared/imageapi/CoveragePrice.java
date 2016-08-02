package com.geocento.webapps.eobroker.common.shared.imageapi;

/**
 *
 * NOT SUPPORTED YET - Coverage prices are the cost of requesting a coverage of an AoI with a sensor
 */
public class CoveragePrice {
    String satellite;
    String instrument;
    Price price;
    Price convertedPrice;
    String priceExplanation;

    public String getSatellite() {
        return satellite;
    }

    public void setSatellite(String satellite) {
        this.satellite = satellite;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Price getPrice() {
        return price;
    }

    public void setConvertedPrice(Price convertedPrice) {
        this.convertedPrice = convertedPrice;
    }

    public Price getConvertedPrice() {
        return convertedPrice;
    }

    public void setPriceExplanation(String priceExplanation) {
        this.priceExplanation = priceExplanation;
    }

    public String getPriceExplanation() {
        return priceExplanation;
    }
}
