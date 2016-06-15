package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ServicesPlace extends EOBrokerPlace {

    public static enum TOKENS {service};

    public ServicesPlace() {
    }

    public ServicesPlace(String token) {
        super(token);
    }

    @Prefix("services")
    public static class Tokenizer implements PlaceTokenizer<ServicesPlace> {
        @Override
        public String getToken(ServicesPlace place) {
            return place.getToken();
        }

        @Override
        public ServicesPlace getPlace(String token) {
            return new ServicesPlace(token);
        }
    }
}
