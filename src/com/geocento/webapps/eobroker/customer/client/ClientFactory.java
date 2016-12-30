package com.geocento.webapps.eobroker.customer.client;

import com.geocento.webapps.eobroker.customer.client.views.*;
import com.geocento.webapps.eobroker.customer.client.views.ProductResponseView;
import com.geocento.webapps.eobroker.customer.client.views.ImagesResponseView;
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

    RequestImageryView getRequestImageryView();

    ProductFormView getProductFormView();

    FullView getFullView();

    ProductFeasibilityView getProductFeasibilityView();

    RequestsView getOrdersView();

    ProductResponseView getProductResponseView();

    ImageryResponseView getImageryResponseView();

    ImagesResponseView getImagesResponseView();

    ConversationView getConversationView();

    VisualisationView getVisualisationView();

    FeedbackView getFeedbackView();
}
