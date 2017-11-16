package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ChallengesPlace extends EOBrokerPlace {

    public ChallengesPlace() {
    }

    public ChallengesPlace(String token) {
        super(token);
    }

    @Prefix("challenges")
    public static class Tokenizer implements PlaceTokenizer<ChallengesPlace> {
        @Override
        public String getToken(ChallengesPlace place) {
            return place.getToken();
        }

        @Override
        public ChallengesPlace getPlace(String token) {
            return new ChallengesPlace(token);
        }
    }
}
