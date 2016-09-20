package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ConversationPlace extends EOBrokerPlace {

    public static enum TOKENS {id};

    public ConversationPlace() {
    }

    public ConversationPlace(String token) {
        super(token);
    }

    @Prefix("conversation")
    public static class Tokenizer implements PlaceTokenizer<ConversationPlace> {
        @Override
        public String getToken(ConversationPlace place) {
            return place.getToken();
        }

        @Override
        public ConversationPlace getPlace(String token) {
            return new ConversationPlace(token);
        }
    }
}
