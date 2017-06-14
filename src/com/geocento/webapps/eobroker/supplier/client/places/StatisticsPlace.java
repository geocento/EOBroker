package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class StatisticsPlace extends EOBrokerPlace {

    static public enum TOKENS {};

    public StatisticsPlace() {
    }

    public StatisticsPlace(String token) {
        super(token);
    }

    @Prefix("statistics")
    public static class Tokenizer implements PlaceTokenizer<StatisticsPlace> {
        @Override
        public String getToken(StatisticsPlace place) {
            return place.getToken();
        }

        @Override
        public StatisticsPlace getPlace(String token) {
            return new StatisticsPlace(token);
        }
    }
}
