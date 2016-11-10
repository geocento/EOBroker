package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProjectPlace extends EOBrokerPlace {

    static public enum TOKENS {id};

    public ProjectPlace() {
    }

    public ProjectPlace(String token) {
        super(token);
    }

    @Prefix("project")
    public static class Tokenizer implements PlaceTokenizer<ProjectPlace> {
        @Override
        public String getToken(ProjectPlace place) {
            return place.getToken();
        }

        @Override
        public ProjectPlace getPlace(String token) {
            return new ProjectPlace(token);
        }
    }
}
