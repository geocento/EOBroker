package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class SoftwarePlace extends EOBrokerPlace {

    static public enum TOKENS {id};

    public SoftwarePlace() {
    }

    public SoftwarePlace(String token) {
        super(token);
    }

    @Prefix("software")
    public static class Tokenizer implements PlaceTokenizer<SoftwarePlace> {
        @Override
        public String getToken(SoftwarePlace place) {
            return place.getToken();
        }

        @Override
        public SoftwarePlace getPlace(String token) {
            return new SoftwarePlace(token);
        }
    }
}
