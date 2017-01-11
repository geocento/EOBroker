package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductServicesPlace extends EOBrokerPlace {

    static public enum TOKENS {};

    public ProductServicesPlace() {
    }

    public ProductServicesPlace(String token) {
        super(token);
    }

    @Prefix("services")
    public static class Tokenizer implements PlaceTokenizer<ProductServicesPlace> {
        @Override
        public String getToken(ProductServicesPlace place) {
            return place.getToken();
        }

        @Override
        public ProductServicesPlace getPlace(String token) {
            return new ProductServicesPlace(token);
        }
    }
}
