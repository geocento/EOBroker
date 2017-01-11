package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductServicePlace extends EOBrokerPlace {

    public static enum TOKENS {service};

    public ProductServicePlace() {
    }

    public ProductServicePlace(String token) {
        super(token);
    }

    @Prefix("service")
    public static class Tokenizer implements PlaceTokenizer<ProductServicePlace> {
        @Override
        public String getToken(ProductServicePlace place) {
            return place.getToken();
        }

        @Override
        public ProductServicePlace getPlace(String token) {
            return new ProductServicePlace(token);
        }
    }
}
