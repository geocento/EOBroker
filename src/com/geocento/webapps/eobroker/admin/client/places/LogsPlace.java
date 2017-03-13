package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class LogsPlace extends EOBrokerPlace {

    static public enum TOKENS {};

    public LogsPlace() {
    }

    public LogsPlace(String token) {
        super(token);
    }

    @Prefix("logs")
    public static class Tokenizer implements PlaceTokenizer<LogsPlace> {
        @Override
        public String getToken(LogsPlace place) {
            return place.getToken();
        }

        @Override
        public LogsPlace getPlace(String token) {
            return new LogsPlace(token);
        }
    }
}
