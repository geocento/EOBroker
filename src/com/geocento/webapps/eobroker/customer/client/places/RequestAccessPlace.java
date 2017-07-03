package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class RequestAccessPlace extends EOBrokerPlace {

    public RequestAccessPlace() {
    }

    public RequestAccessPlace(String token) {
        super(token);
    }

    @Prefix("requestAccess")
    public static class Tokenizer implements PlaceTokenizer<RequestAccessPlace> {
        @Override
        public String getToken(RequestAccessPlace place) {
            return place.getToken();
        }

        @Override
        public RequestAccessPlace getPlace(String token) {
            return new RequestAccessPlace(token);
        }
    }
}
