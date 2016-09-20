package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ImageryResponsePlace extends EOBrokerPlace {

    public enum TOKENS {id, responseid};

    public ImageryResponsePlace() {
    }

    public ImageryResponsePlace(String token) {
        super(token);
    }

    @Prefix("imageryresponse")
    public static class Tokenizer implements PlaceTokenizer<ImageryResponsePlace> {
        @Override
        public String getToken(ImageryResponsePlace place) {
            return place.getToken();
        }

        @Override
        public ImageryResponsePlace getPlace(String token) {
            return new ImageryResponsePlace(token);
        }
    }
}
