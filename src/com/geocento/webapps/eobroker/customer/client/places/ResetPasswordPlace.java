package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ResetPasswordPlace extends EOBrokerPlace {

    public enum TOKENS {username, resetToken};

    private Place nextPlace;

    public ResetPasswordPlace() {
    }

    public ResetPasswordPlace(String token) {
        super(token);
    }

    public ResetPasswordPlace(Place nextPlace) {
        this.nextPlace = nextPlace;
    }

    public Place getNextPlace() {
        return nextPlace;
    }

    @Prefix("pwdrset")
    public static class Tokenizer implements PlaceTokenizer<ResetPasswordPlace> {
        @Override
        public String getToken(ResetPasswordPlace place) {
            return place.getToken();
        }

        @Override
        public ResetPasswordPlace getPlace(String token) {
            return new ResetPasswordPlace(token);
        }
    }
}
