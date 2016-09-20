package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class RequestsPlace extends EOBrokerPlace {

    public RequestsPlace() {
    }

    public RequestsPlace(String token) {
        super(token);
    }

    public enum TOKENS {};

    @Prefix("orders")
    public static class Tokenizer implements PlaceTokenizer<RequestsPlace> {
        @Override
        public String getToken(RequestsPlace place) {
            return place.getToken();
        }

        @Override
        public RequestsPlace getPlace(String token) {
            return new RequestsPlace(token);
        }
    }
}
