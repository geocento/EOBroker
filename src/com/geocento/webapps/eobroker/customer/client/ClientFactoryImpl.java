package com.geocento.webapps.eobroker.customer.client;

import com.geocento.webapps.eobroker.customer.client.places.LandingPagePlace;
import com.geocento.webapps.eobroker.customer.client.views.*;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class ClientFactoryImpl implements ClientFactory {

    private final EventBus eventBus = new SimpleEventBus();

    private final PlaceController placeController = new PlaceController(eventBus);

    private LandingPageView landingPageView = null;

    private SearchPageView searchPageView = null;

    private LoginPageViewImpl loginPageView = null;

    private ImageSearchViewImpl imageSearchView = null;

    private RequestImageryViewImpl requestImageryView = null;

    private ProductFormViewImpl productFormView = null;

    private FullViewImpl fullView = null;

    private ProductFeasibilityViewImpl productFeasibilityView = null;

    private OrdersViewImpl ordersView = null;

    private OrderViewImpl orderView = null;

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
        return new LandingPagePlace();
    }

    @Override
    public LandingPageView getLandingPageView() {
        if(landingPageView == null) {
            landingPageView = new LandingPageViewImpl(this);
        }
        return landingPageView;
    }

    @Override
    public LoginPageView getLoginPageView() {
        if(loginPageView == null) {
            loginPageView = new LoginPageViewImpl(this);
        }
        return loginPageView;
    }

    @Override
    public SearchPageView getSearchPageView() {
        if(searchPageView == null) {
            searchPageView = new SearchPageViewImpl(this);
        }
        return searchPageView;
    }

    @Override
    public ImageSearchView getImageSearchView() {
        if(imageSearchView == null) {
            imageSearchView = new ImageSearchViewImpl(this);
        }
        return imageSearchView;
    }

    @Override
    public RequestImageryView getRequestImageryView() {
        if(requestImageryView == null) {
            requestImageryView = new RequestImageryViewImpl(this);
        }
        return requestImageryView;
    }

    @Override
    public ProductFormView getProductFormView() {
        if(productFormView == null) {
            productFormView = new ProductFormViewImpl(this);
        }
        return productFormView;
    }

    @Override
    public FullView getFullView() {
        if(fullView == null) {
            fullView = new FullViewImpl(this);
        }
        return fullView;
    }

    @Override
    public ProductFeasibilityView getProductFeasibilityView() {
        if(productFeasibilityView == null) {
            productFeasibilityView = new ProductFeasibilityViewImpl(this);
        }
        return productFeasibilityView;
    }

    @Override
    public OrdersView getOrdersView() {
        if(ordersView == null) {
            ordersView = new OrdersViewImpl(this);
        }
        return ordersView;
    }

    @Override
    public OrderView getOrderView() {
        if(orderView == null) {
            orderView = new OrderViewImpl(this);
        }
        return orderView;
    }

}

