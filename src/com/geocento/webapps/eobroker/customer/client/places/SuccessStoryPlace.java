package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class SuccessStoryPlace extends EOBrokerPlace {

    static public enum TOKENS {id};

    public SuccessStoryPlace() {
    }

    public SuccessStoryPlace(String token) {
        super(token);
    }

    @Prefix("story")
    public static class Tokenizer implements PlaceTokenizer<SuccessStoryPlace> {
        @Override
        public String getToken(SuccessStoryPlace place) {
            return place.getToken();
        }

        @Override
        public SuccessStoryPlace getPlace(String token) {
            return new SuccessStoryPlace(token);
        }
    }
}
