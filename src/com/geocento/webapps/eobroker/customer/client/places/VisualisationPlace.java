package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class VisualisationPlace extends EOBrokerPlace {

    public VisualisationPlace() {
    }

    public VisualisationPlace(String token) {
        super(token);
    }

    public enum TOKENS {productDatasetId, productServiceId, dataAccessId};

    @Prefix("visualisation")
    public static class Tokenizer implements PlaceTokenizer<VisualisationPlace> {
        @Override
        public String getToken(VisualisationPlace place) {
            return place.getToken();
        }

        @Override
        public VisualisationPlace getPlace(String token) {
            return new VisualisationPlace(token);
        }
    }
}
