package com.geocento.webapps.eobroker.admin.client;

import com.geocento.webapps.eobroker.admin.client.places.DashboardPlace;
import com.geocento.webapps.eobroker.admin.client.views.*;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class ClientFactoryImpl implements ClientFactory {

    private final EventBus eventBus = new SimpleEventBus();

    private final PlaceController placeController = new PlaceController(eventBus);

    private LoginPageView loginPageView = null;

    private DashboardViewImpl dashboardView = null;

    private ProductsViewImpl productsView = null;

    private CompaniesViewImpl companiesView = null;

    private CompanyViewImpl companyView = null;

    private ProductViewImpl productView = null;

    private NewsItemsViewImpl newsItemsView = null;

    private NewsItemViewImpl newsItemView = null;

    private DatasetProvidersView datasetProvidersView = null;

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
    public LoginPageView getLoginPageView() {
        if(loginPageView == null) {
            loginPageView = new LoginPageViewImpl(this);
        }
        return loginPageView;
    }

    @Override
    public DashboardView getDashboardView() {
        if(dashboardView == null) {
            dashboardView = new DashboardViewImpl(this);
        }
        return dashboardView;
    }

    @Override
    public ProductsView getProductsView() {
        if(productsView == null) {
            productsView = new ProductsViewImpl(this);
        }
        return productsView;
    }

    @Override
    public ProductView getProductView() {
        if(productView == null) {
            productView = new ProductViewImpl(this);
        }
        return productView;
    }

    @Override
    public NewsItemsView getNewsItemsView() {
        if(newsItemsView == null) {
            newsItemsView = new NewsItemsViewImpl(this);
        }
        return newsItemsView;
    }

    @Override
    public NewsItemView getNewsItemView() {
        if(newsItemView == null) {
            newsItemView = new NewsItemViewImpl(this);
        }
        return newsItemView;
    }

    @Override
    public CompaniesView getCompaniesView() {
        if(companiesView == null) {
            companiesView = new CompaniesViewImpl(this);
        }
        return companiesView;
    }

    @Override
    public CompanyView getCompanyView() {
        if(companyView == null) {
            companyView = new CompanyViewImpl(this);
        }
        return companyView;
    }

    @Override
    public DatasetProvidersView getDatasetProvidersView() {
        if(datasetProvidersView == null) {
            datasetProvidersView = new DatasetProvidersViewImpl(this);
        }
        return datasetProvidersView;
    }

}

