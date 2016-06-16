package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ImageSearchPlace extends EOBrokerPlace {

    public ImageSearchPlace() {
    }

    public ImageSearchPlace(String token) {
        super(token);
    }

    public enum TOKENS {text, aoiId, uri, product};

    @Prefix("imagesearch")
    public static class Tokenizer implements PlaceTokenizer<ImageSearchPlace> {
        @Override
        public String getToken(ImageSearchPlace place) {
            return place.getToken();
        }

        @Override
        public ImageSearchPlace getPlace(String token) {
            return new ImageSearchPlace(token);
        }
    }
}
