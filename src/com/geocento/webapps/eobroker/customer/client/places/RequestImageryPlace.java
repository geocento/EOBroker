package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class RequestImageryPlace extends EOBrokerPlace {

    public RequestImageryPlace() {
    }

    public RequestImageryPlace(String token) {
        super(token);
    }

    @Prefix("requestimagery")
    public static class Tokenizer implements PlaceTokenizer<RequestImageryPlace> {
        @Override
        public String getToken(RequestImageryPlace place) {
            return place.getToken();
        }

        @Override
        public RequestImageryPlace getPlace(String token) {
            return new RequestImageryPlace(token);
        }
    }
}
