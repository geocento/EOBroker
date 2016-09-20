package com.geocento.webapps.eobroker.supplier.client;

import com.geocento.webapps.eobroker.supplier.client.views.*;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {

    EventBus getEventBus();

    PlaceController getPlaceController();

    Place getDefaultPlace();

    DashboardView getDashboardView();

    LoginPageView getLoginPageView();

    CompanyView getCompanyView();

    ServicesView getServicesView();

    OrdersView getOrdersView();

    OrderView getOrderView();

    ConversationView getConversationView();
}
