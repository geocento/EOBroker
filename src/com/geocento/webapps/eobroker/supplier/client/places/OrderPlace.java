package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class OrderPlace extends EOBrokerPlace {

    public static enum TOKENS {id};

    public OrderPlace() {
    }

    public OrderPlace(String token) {
        super(token);
    }

    @Prefix("order")
    public static class Tokenizer implements PlaceTokenizer<OrderPlace> {
        @Override
        public String getToken(OrderPlace place) {
            return place.getToken();
        }

        @Override
        public OrderPlace getPlace(String token) {
            return new OrderPlace(token);
        }
    }
}
