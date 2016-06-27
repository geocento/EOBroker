package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductFormPlace extends EOBrokerPlace {

    public static enum TOKENS {id, serviceid};

    public ProductFormPlace() {
    }

    public ProductFormPlace(Long id) {
        this(TOKENS.id.toString() + "=" + id);
    }

    public ProductFormPlace(String token) {
        super(token);
    }

    @Prefix("productform")
    public static class Tokenizer implements PlaceTokenizer<ProductFormPlace> {
        @Override
        public String getToken(ProductFormPlace place) {
            return place.getToken();
        }

        @Override
        public ProductFormPlace getPlace(String token) {
            return new ProductFormPlace(token);
        }
    }
}
