package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class DatasetProviderPlace extends EOBrokerPlace {

    static public enum TOKENS {id};

    public DatasetProviderPlace() {
    }

    public DatasetProviderPlace(String token) {
        super(token);
    }

    @Prefix("dataset")
    public static class Tokenizer implements PlaceTokenizer<DatasetProviderPlace> {
        @Override
        public String getToken(DatasetProviderPlace place) {
            return place.getToken();
        }

        @Override
        public DatasetProviderPlace getPlace(String token) {
            return new DatasetProviderPlace(token);
        }
    }
}
