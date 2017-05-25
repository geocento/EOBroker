package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class CatalogueSearchPlace extends EOBrokerPlace {

    public CatalogueSearchPlace() {
    }

    public CatalogueSearchPlace(String token) {
        super(token);
    }

    public enum TOKENS {text, aoiId, start, stop, aoiWKT, uri, productId};

    @Prefix("cataloguesearch")
    public static class Tokenizer implements PlaceTokenizer<CatalogueSearchPlace> {
        @Override
        public String getToken(CatalogueSearchPlace place) {
            return place.getToken();
        }

        @Override
        public CatalogueSearchPlace getPlace(String token) {
            return new CatalogueSearchPlace(token);
        }
    }
}
