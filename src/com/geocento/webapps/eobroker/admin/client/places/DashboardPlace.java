package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class DashboardPlace extends EOBrokerPlace {

    public DashboardPlace() {
    }

    public DashboardPlace(String token) {
        super(token);
    }

    @Prefix("dashboard")
    public static class Tokenizer implements PlaceTokenizer<DashboardPlace> {
        @Override
        public String getToken(DashboardPlace place) {
            return place.getToken();
        }

        @Override
        public DashboardPlace getPlace(String token) {
            return new DashboardPlace(token);
        }
    }
}
