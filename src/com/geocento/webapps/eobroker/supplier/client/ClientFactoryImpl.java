package com.geocento.webapps.eobroker.supplier.client;

import com.geocento.webapps.eobroker.supplier.client.places.DashboardPlace;
import com.geocento.webapps.eobroker.supplier.client.views.*;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class ClientFactoryImpl implements ClientFactory {

    private final EventBus eventBus = new SimpleEventBus();

    private final PlaceController placeController = new PlaceController(eventBus);

    private LoginPageView loginPageView = null;
    private DashboardView dashboardView = null;
    private CompanyView companyView = null;
    private ServicesView servicesView = null;
    private OrdersView ordersView = null;

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PlaceController getPlaceController() {
        return placeController;
    }

    @Override
    public Place getDefaultPlace() {
        return new DashboardPlace("");
    }

    @Override
    public DashboardView getDashboardView() {
        if(dashboardView == null) {
            dashboardView = new DashboardViewImpl(this);
        }
        return dashboardView;
    }

    @Override
    public LoginPageView getLoginPageView() {
        if(loginPageView == null) {
            loginPageView = new LoginPageViewImpl(this);
        }
        return loginPageView;
    }

    @Override
    public CompanyView getCompanyView() {
        if(companyView == null) {
            companyView = new CompanyViewImpl(this);
        }
        return companyView;
    }

    @Override
    public ServicesView getServicesView() {
        if(servicesView == null) {
            servicesView = new ServicesViewImpl(this);
        }
        return servicesView;
    }

    @Override
    public OrdersView getOrdersView() {
        if(ordersView == null) {
            ordersView = new OrdersViewImpl(this);
        }
        return ordersView;
    }

}
