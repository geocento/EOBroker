package com.geocento.webapps.eobroker.client.activities;

import com.geocento.webapps.eobroker.client.ClientFactory;
import com.geocento.webapps.eobroker.client.places.ImageSearchPlace;
import com.geocento.webapps.eobroker.client.places.LandingPagePlace;
import com.geocento.webapps.eobroker.client.places.LoginPagePlace;
import com.geocento.webapps.eobroker.client.places.SearchPagePlace;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {

    private ClientFactory clientFactory;

    public AppActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
    	if (place instanceof LandingPagePlace) {
            return new LandingPageActivity((LandingPagePlace) place, clientFactory);
        } else if(place instanceof SearchPagePlace) {
            return new SearchPageActivity((SearchPagePlace) place, clientFactory);
        } else if(place instanceof ImageSearchPlace) {
            return new ImageSearchActivity((ImageSearchPlace) place, clientFactory);
        } else if(place instanceof LoginPagePlace) {
            return new LoginPageActivity((LoginPagePlace) place, clientFactory);
        }
        return null;
    }

}
