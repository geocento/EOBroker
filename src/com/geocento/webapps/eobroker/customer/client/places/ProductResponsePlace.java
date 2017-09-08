package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductResponsePlace extends EOBrokerPlace {

    public enum TOKENS {id, responseid, otsresponseid};

    public ProductResponsePlace() {
    }

    public ProductResponsePlace(String token) {
        super(token);
    }

    @Prefix("productresponse")
    public static class Tokenizer implements PlaceTokenizer<ProductResponsePlace> {
        @Override
        public String getToken(ProductResponsePlace place) {
            return place.getToken();
        }

        @Override
        public ProductResponsePlace getPlace(String token) {
            return new ProductResponsePlace(token);
        }
    }
}
