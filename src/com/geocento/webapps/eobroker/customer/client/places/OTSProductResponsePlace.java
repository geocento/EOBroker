package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class OTSProductResponsePlace extends EOBrokerPlace {

    public enum TOKENS {id, responseid};

    public OTSProductResponsePlace() {
    }

    public OTSProductResponsePlace(String token) {
        super(token);
    }

    @Prefix("otsproductresponse")
    public static class Tokenizer implements PlaceTokenizer<OTSProductResponsePlace> {
        @Override
        public String getToken(OTSProductResponsePlace place) {
            return place.getToken();
        }

        @Override
        public OTSProductResponsePlace getPlace(String token) {
            return new OTSProductResponsePlace(token);
        }
    }
}
