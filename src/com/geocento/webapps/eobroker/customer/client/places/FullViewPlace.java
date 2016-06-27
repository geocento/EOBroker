package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class FullViewPlace extends EOBrokerPlace {

    public static enum TOKENS {productid, companyid, productserviceid};

    public FullViewPlace() {
    }

    public FullViewPlace(String token) {
        super(token);
    }

    @Prefix("fullview")
    public static class Tokenizer implements PlaceTokenizer<FullViewPlace> {
        @Override
        public String getToken(FullViewPlace place) {
            return place.getToken();
        }

        @Override
        public FullViewPlace getPlace(String token) {
            return new FullViewPlace(token);
        }
    }
}
