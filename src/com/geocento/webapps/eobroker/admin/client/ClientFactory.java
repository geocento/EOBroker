package com.geocento.webapps.eobroker.admin.client;

import com.geocento.webapps.eobroker.admin.client.views.*;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {

    EventBus getEventBus();

    PlaceController getPlaceController();

    Place getDefaultPlace();

    LoginPageView getLoginPageView();

    StatsView getStatsView();

    ProductsView getProductsView();

    CompaniesView getCompaniesView();

    ProductView getProductView();

    NewsItemsView getNewsItemsView();

    NewsItemView getNewsItemView();

    CompanyView getCompanyView();

    DatasetProvidersView getDatasetProvidersView();

    FeedbackView getFeedbackView();

    UsersView getUsersView();

    SettingsView getSettingsView();

    LogsView getLogsView();

    ChallengesView getChallengesView();

    ChallengeView getChallengeView();

    ProductCategoriesView getProductCategoriesView();
}
