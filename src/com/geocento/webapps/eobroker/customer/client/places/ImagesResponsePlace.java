package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ImagesResponsePlace extends EOBrokerPlace {

    public enum TOKENS {id};

    public ImagesResponsePlace() {
    }

    public ImagesResponsePlace(String token) {
        super(token);
    }

    @Prefix("imagesresponse")
    public static class Tokenizer implements PlaceTokenizer<ImagesResponsePlace> {
        @Override
        public String getToken(ImagesResponsePlace place) {
            return place.getToken();
        }

        @Override
        public ImagesResponsePlace getPlace(String token) {
            return new ImagesResponsePlace(token);
        }
    }
}
