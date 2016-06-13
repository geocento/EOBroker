package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class LandingPagePlace extends EOBrokerPlace {

    public LandingPagePlace() {
    }

    public LandingPagePlace(String token) {
        super(token);
    }

    @Prefix("landing")
    public static class Tokenizer implements PlaceTokenizer<LandingPagePlace> {
        @Override
        public String getToken(LandingPagePlace place) {
            return place.getToken();
        }

        @Override
        public LandingPagePlace getPlace(String token) {
            return new LandingPagePlace(token);
        }
    }
}
