package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class SearchPagePlace extends EOBrokerPlace {

    public SearchPagePlace() {
    }

    public SearchPagePlace(String token) {
        super(token);
    }

    public enum TOKENS {text, category, aoiId, product, browse};

    @Prefix("search")
    public static class Tokenizer implements PlaceTokenizer<SearchPagePlace> {
        @Override
        public String getToken(SearchPagePlace place) {
            return place.getToken();
        }

        @Override
        public SearchPagePlace getPlace(String token) {
            return new SearchPagePlace(token);
        }
    }
}
