package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class SettingsPlace extends EOBrokerPlace {

    static public enum TOKENS {};

    public SettingsPlace() {
    }

    public SettingsPlace(String token) {
        super(token);
    }

    @Prefix("settings")
    public static class Tokenizer implements PlaceTokenizer<SettingsPlace> {
        @Override
        public String getToken(SettingsPlace place) {
            return place.getToken();
        }

        @Override
        public SettingsPlace getPlace(String token) {
            return new SettingsPlace(token);
        }
    }
}
