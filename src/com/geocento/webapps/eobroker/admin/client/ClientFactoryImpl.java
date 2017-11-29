package com.geocento.webapps.eobroker.admin.client;

import com.geocento.webapps.eobroker.admin.client.places.CompaniesPlace;
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

    private ProductCategoriesView productCategoriesView = null;

    private ChallengesViewImpl challengesView = null;

    private ChallengeViewImpl challengeView = null;

    private NewsItemsViewImpl newsItemsView = null;

    private NewsItemViewImpl newsItemView = null;

    private DatasetProvidersView datasetProvidersView = null;

    private FeedbackView feedbackView = null;

    private UsersView usersView = null;

    private SettingsView settingsView = null;

    private LogsView logsView = null;

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
        return new CompaniesPlace("");
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

    @Override
    public FeedbackView getFeedbackView() {
        if(feedbackView == null) {
            feedbackView = new FeedbackViewImpl(this);
        }
        return feedbackView;
    }

    @Override
    public UsersView getUsersView() {
        if(usersView == null) {
            usersView = new UsersViewImpl(this);
        }
        return usersView;
    }

    @Override
    public SettingsView getSettingsView() {
        if(settingsView == null) {
            settingsView = new SettingsViewImpl(this);
        }
        return settingsView;
    }

    @Override
    public LogsView getLogsView() {
        if(logsView == null) {
            logsView = new LogsViewImpl(this);
        }
        return logsView;
    }

    @Override
    public ChallengesView getChallengesView() {
        if(challengesView == null) {
            challengesView = new ChallengesViewImpl(this);
        }
        return challengesView;
    }

    @Override
    public ChallengeView getChallengeView() {
        if(challengeView == null) {
            challengeView = new ChallengeViewImpl(this);
        }
        return challengeView;
    }

    @Override
    public ProductCategoriesView getProductCategoriesView() {
        if(productCategoriesView == null) {
            productCategoriesView = new ProductCategoriesViewImpl(this);
        }
        return productCategoriesView;
    }

}

