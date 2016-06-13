package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.*;
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
        if(place instanceof DashboardPlace) {
            return new DashboardActivity((DashboardPlace) place, clientFactory);
        } else if(place instanceof LoginPagePlace) {
            return new LoginActivity((LoginPagePlace) place, clientFactory);
        } else if(place instanceof CompanyPlace) {
            return new CompanyActivity((CompanyPlace) place, clientFactory);
        } else if(place instanceof ServicesPlace) {
            return new ServicesActivity((ServicesPlace) place, clientFactory);
        } else if(place instanceof OrdersPlace) {
            return new OrdersActivity((OrdersPlace) place, clientFactory);
        }
        return null;
    }

}