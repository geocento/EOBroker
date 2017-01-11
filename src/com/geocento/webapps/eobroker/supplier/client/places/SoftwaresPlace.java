package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class SoftwaresPlace extends EOBrokerPlace {

    static public enum TOKENS {};

    public SoftwaresPlace() {
    }

    public SoftwaresPlace(String token) {
        super(token);
    }

    @Prefix("softwares")
    public static class Tokenizer implements PlaceTokenizer<SoftwaresPlace> {
        @Override
        public String getToken(SoftwaresPlace place) {
            return place.getToken();
        }

        @Override
        public SoftwaresPlace getPlace(String token) {
            return new SoftwaresPlace(token);
        }
    }
}
