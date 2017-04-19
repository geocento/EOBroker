package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class TestimonialsPlace extends EOBrokerPlace {

    public static enum TOKENS {};

    public TestimonialsPlace() {
    }

    public TestimonialsPlace(String token) {
        super(token);
    }

    @Prefix("testimonials")
    public static class Tokenizer implements PlaceTokenizer<TestimonialsPlace> {
        @Override
        public String getToken(TestimonialsPlace place) {
            return place.getToken();
        }

        @Override
        public TestimonialsPlace getPlace(String token) {
            return new TestimonialsPlace(token);
        }
    }
}
