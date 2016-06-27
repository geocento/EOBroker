package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductPlace extends EOBrokerPlace {

    static public enum TOKENS {id};

    public ProductPlace() {
    }

    public ProductPlace(Long id) {
        token = TOKENS.id.toString() + "=" + id.longValue();
    }

    public ProductPlace(String token) {
        super(token);
    }

    @Prefix("products")
    public static class Tokenizer implements PlaceTokenizer<ProductPlace> {
        @Override
        public String getToken(ProductPlace place) {
            return place.getToken();
        }

        @Override
        public ProductPlace getPlace(String token) {
            return new ProductPlace(token);
        }
    }
}
