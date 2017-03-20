package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class SuccessStoriesPlace extends EOBrokerPlace {

    static public enum TOKENS {};

    public SuccessStoriesPlace() {
    }

    public SuccessStoriesPlace(String token) {
        super(token);
    }

    @Prefix("stories")
    public static class Tokenizer implements PlaceTokenizer<SuccessStoriesPlace> {
        @Override
        public String getToken(SuccessStoriesPlace place) {
            return place.getToken();
        }

        @Override
        public SuccessStoriesPlace getPlace(String token) {
            return new SuccessStoriesPlace(token);
        }
    }
}
