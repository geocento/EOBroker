package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class TestimonialPlace extends EOBrokerPlace {

    public static enum TOKENS {category, id};

    public TestimonialPlace() {
    }

    public TestimonialPlace(String token) {
        super(token);
    }

    @Prefix("testimonial")
    public static class Tokenizer implements PlaceTokenizer<TestimonialPlace> {
        @Override
        public String getToken(TestimonialPlace place) {
            return place.getToken();
        }

        @Override
        public TestimonialPlace getPlace(String token) {
            return new TestimonialPlace(token);
        }
    }
}
