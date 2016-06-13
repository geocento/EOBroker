package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class OrdersPlace extends EOBrokerPlace {

    public OrdersPlace() {
    }

    public OrdersPlace(String token) {
        super(token);
    }

    @Prefix("orders")
    public static class Tokenizer implements PlaceTokenizer<OrdersPlace> {
        @Override
        public String getToken(OrdersPlace place) {
            return place.getToken();
        }

        @Override
        public OrdersPlace getPlace(String token) {
            return new OrdersPlace(token);
        }
    }
}
