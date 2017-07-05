package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class UsersPlace extends EOBrokerPlace {

    public static enum TOKENS {name};

    public UsersPlace() {
    }

    public UsersPlace(String token) {
        super(token);
    }

    @Prefix("users")
    public static class Tokenizer implements PlaceTokenizer<UsersPlace> {
        @Override
        public String getToken(UsersPlace place) {
            return place.getToken();
        }

        @Override
        public UsersPlace getPlace(String token) {
            return new UsersPlace(token);
        }
    }
}
