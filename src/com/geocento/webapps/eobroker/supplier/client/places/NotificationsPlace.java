package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class NotificationsPlace extends EOBrokerPlace {

    public static enum TOKENS {};

    public NotificationsPlace() {
    }

    public NotificationsPlace(String token) {
        super(token);
    }

    @Prefix("notifications")
    public static class Tokenizer implements PlaceTokenizer<NotificationsPlace> {
        @Override
        public String getToken(NotificationsPlace place) {
            return place.getToken();
        }

        @Override
        public NotificationsPlace getPlace(String token) {
            return new NotificationsPlace(token);
        }
    }
}
