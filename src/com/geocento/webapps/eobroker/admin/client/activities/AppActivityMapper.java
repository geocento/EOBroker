package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.*;
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
    	if (place instanceof LoginPagePlace) {
            return new LoginPageActivity((LoginPagePlace) place, clientFactory);
        } else if(place instanceof DashboardPlace) {
            return new DashboardActivity((DashboardPlace) place, clientFactory);
        } else if(place instanceof UsersPlace) {
            return new UsersActivity((UsersPlace) place, clientFactory);
        } else if(place instanceof CompanyPlace) {
            return new CompanyActivity((CompanyPlace) place, clientFactory);
        } else if(place instanceof CompaniesPlace) {
            return new CompaniesActivity((CompaniesPlace) place, clientFactory);
        } else if(place instanceof ProductsPlace) {
            return new ProductsActivity((ProductsPlace) place, clientFactory);
        } else if(place instanceof ProductPlace) {
            return new ProductActivity((ProductPlace) place, clientFactory);
        } else if(place instanceof NewsItemsPlace) {
            return new NewsItemsActivity((NewsItemsPlace) place, clientFactory);
        } else if(place instanceof NewsItemPlace) {
            return new NewsItemActivity((NewsItemPlace) place, clientFactory);
        } else if(place instanceof FeedbackPlace) {
            return new FeedbackActivity((FeedbackPlace) place, clientFactory);
        } else if(place instanceof SettingsPlace) {
            return new SettingsActivity((SettingsPlace) place, clientFactory);
        } else if(place instanceof LogsPlace) {
            return new LogsActivity((LogsPlace) place, clientFactory);
        }
        return null;
    }

}
