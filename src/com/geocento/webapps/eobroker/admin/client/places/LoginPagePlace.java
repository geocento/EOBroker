package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class LoginPagePlace extends EOBrokerPlace {

    public LoginPagePlace() {
    }

    public LoginPagePlace(String token) {
        super(token);
    }

    @Prefix("login")
    public static class Tokenizer implements PlaceTokenizer<LoginPagePlace> {
        @Override
        public String getToken(LoginPagePlace place) {
            return place.getToken();
        }

        @Override
        public LoginPagePlace getPlace(String token) {
            return new LoginPagePlace(token);
        }
    }
}
