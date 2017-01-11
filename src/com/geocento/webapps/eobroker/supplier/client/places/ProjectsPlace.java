package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProjectsPlace extends EOBrokerPlace {

    static public enum TOKENS {};

    public ProjectsPlace() {
    }

    public ProjectsPlace(String token) {
        super(token);
    }

    @Prefix("projects")
    public static class Tokenizer implements PlaceTokenizer<ProjectsPlace> {
        @Override
        public String getToken(ProjectsPlace place) {
            return place.getToken();
        }

        @Override
        public ProjectsPlace getPlace(String token) {
            return new ProjectsPlace(token);
        }
    }
}
