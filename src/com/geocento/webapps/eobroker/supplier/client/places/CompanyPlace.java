package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class CompanyPlace extends EOBrokerPlace {

    static public enum TOKENS {id};

    public CompanyPlace() {
    }

    public CompanyPlace(String token) {
        super(token);
    }

    @Prefix("company")
    public static class Tokenizer implements PlaceTokenizer<CompanyPlace> {
        @Override
        public String getToken(CompanyPlace place) {
            return place.getToken();
        }

        @Override
        public CompanyPlace getPlace(String token) {
            return new CompanyPlace(token);
        }
    }
}
