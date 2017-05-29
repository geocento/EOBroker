package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ConversationsPlace extends EOBrokerPlace {

    public static enum TOKENS {};

    public ConversationsPlace() {
    }

    public ConversationsPlace(String token) {
        super(token);
    }

    @Prefix("conversations")
    public static class Tokenizer implements PlaceTokenizer<ConversationsPlace> {
        @Override
        public String getToken(ConversationsPlace place) {
            return place.getToken();
        }

        @Override
        public ConversationsPlace getPlace(String token) {
            return new ConversationsPlace(token);
        }
    }
}
