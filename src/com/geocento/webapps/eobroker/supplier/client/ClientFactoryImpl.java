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
    private ProductServiceView productServiceView = null;
    private OrdersView ordersView = null;
    private OrderView orderView = null;
    private ConversationView conversationView = null;
    private DatasetProviderView datasetProviderView = null;
    private ProductDatasetView productDatasetView = null;
    private SoftwareView softwareView = null;
    private ProjectView projectView = null;
    private SuccessStoryView successStoryView = null;

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
    public ProductServiceView getProductServiceView() {
        if(productServiceView == null) {
            productServiceView = new ProductServiceViewImpl(this);
        }
        return productServiceView;
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

    @Override
    public ConversationView getConversationView() {
        if(conversationView == null) {
            conversationView = new ConversationViewImpl(this);
        }
        return conversationView;
    }

    @Override
    public DatasetProviderView getDatasetProviderView() {
        if(datasetProviderView == null) {
            datasetProviderView = new DatasetProviderViewImpl(this);
        }
        return datasetProviderView;
    }

    @Override
    public ProductDatasetView getProductDatasetView() {
        if(productDatasetView == null) {
            productDatasetView = new ProductDatasetViewImpl(this);
        }
        return productDatasetView;
    }

    @Override
    public SoftwareView getSoftwareView() {
        if(softwareView == null) {
            softwareView = new SoftwareViewImpl(this);
        }
        return softwareView;
    }

    @Override
    public ProjectView getProjectView() {
        if(projectView == null) {
            projectView = new ProjectViewImpl(this);
        }
        return projectView;
    }

    @Override
    public SuccessStoryView getSuccessStoryView() {
        if(successStoryView == null) {
            successStoryView = new SuccessStoryViewImpl(this);
        }
        return successStoryView;
    }

}

