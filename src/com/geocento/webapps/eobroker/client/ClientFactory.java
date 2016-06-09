package com.geocento.webapps.eobroker.client;

import com.geocento.webapps.eobroker.client.views.ImageSearchView;
import com.geocento.webapps.eobroker.client.views.LandingPageView;
import com.geocento.webapps.eobroker.client.views.LoginPageView;
import com.geocento.webapps.eobroker.client.views.SearchPageView;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {

    EventBus getEventBus();

    PlaceController getPlaceController();

    Place getDefaultPlace();

    LandingPageView getLandingPageView();

    LoginPageView getLoginPageView();

    SearchPageView getSearchPageView();

    ImageSearchView getImageSearchView();
}
