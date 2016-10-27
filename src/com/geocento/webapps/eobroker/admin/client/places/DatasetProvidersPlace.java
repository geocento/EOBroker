package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class DatasetProvidersPlace extends EOBrokerPlace {

    public DatasetProvidersPlace() {
    }

    public DatasetProvidersPlace(String token) {
        super(token);
    }

    @Prefix("datasets")
    public static class Tokenizer implements PlaceTokenizer<DatasetProvidersPlace> {
        @Override
        public String getToken(DatasetProvidersPlace place) {
            return place.getToken();
        }

        @Override
        public DatasetProvidersPlace getPlace(String token) {
            return new DatasetProvidersPlace(token);
        }
    }
}
