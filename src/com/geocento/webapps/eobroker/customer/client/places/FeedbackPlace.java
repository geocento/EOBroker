package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class FeedbackPlace extends EOBrokerPlace {

    public static enum TOKENS {feedbackid, topic};

    public FeedbackPlace() {
    }

    public FeedbackPlace(String token) {
        super(token);
    }

    @Prefix("feedback")
    public static class Tokenizer implements PlaceTokenizer<FeedbackPlace> {
        @Override
        public String getToken(FeedbackPlace place) {
            return place.getToken();
        }

        @Override
        public FeedbackPlace getPlace(String token) {
            return new FeedbackPlace(token);
        }
    }
}
