package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ChallengePlace extends EOBrokerPlace {

    static public enum TOKENS {id};

    public ChallengePlace() {
    }

    public ChallengePlace(String token) {
        super(token);
    }

    @Prefix("challenge")
    public static class Tokenizer implements PlaceTokenizer<ChallengePlace> {
        @Override
        public String getToken(ChallengePlace place) {
            return place.getToken();
        }

        @Override
        public ChallengePlace getPlace(String token) {
            return new ChallengePlace(token);
        }
    }
}
