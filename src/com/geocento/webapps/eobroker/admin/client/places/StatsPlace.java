package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class StatsPlace extends EOBrokerPlace {

    public StatsPlace() {
    }

    public StatsPlace(String token) {
        super(token);
    }

    @Prefix("stats")
    public static class Tokenizer implements PlaceTokenizer<StatsPlace> {
        @Override
        public String getToken(StatsPlace place) {
            return place.getToken();
        }

        @Override
        public StatsPlace getPlace(String token) {
            return new StatsPlace(token);
        }
    }
}
