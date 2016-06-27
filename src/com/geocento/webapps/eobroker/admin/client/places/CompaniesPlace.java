package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class CompaniesPlace extends EOBrokerPlace {

    public CompaniesPlace() {
    }

    public CompaniesPlace(String token) {
        super(token);
    }

    @Prefix("companies")
    public static class Tokenizer implements PlaceTokenizer<CompaniesPlace> {
        @Override
        public String getToken(CompaniesPlace place) {
            return place.getToken();
        }

        @Override
        public CompaniesPlace getPlace(String token) {
            return new CompaniesPlace(token);
        }
    }
}
