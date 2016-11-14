package com.geocento.webapps.eobroker.customer.client;

import com.geocento.webapps.eobroker.customer.client.places.LandingPagePlace;
import com.geocento.webapps.eobroker.customer.client.views.*;
import com.geocento.webapps.eobroker.customer.client.views.ProductResponseView;
import com.geocento.webapps.eobroker.customer.client.views.ProductResponseViewImpl;
import com.geocento.webapps.eobroker.customer.client.views.ImagesResponseView;
import com.geocento.webapps.eobroker.customer.client.views.ImagesResponseViewImpl;
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

    private RequestsViewImpl ordersView = null;

    private ProductResponseViewImpl productResponseView = null;

    private ImageryResponseViewImpl imageryResponseView = null;

    private ImagesResponseView imagesResponseView = null;

    private VisualisationView visualisationView = null;

    private ConversationViewImpl conversationView = null;

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
    public RequestsView getOrdersView() {
        if(ordersView == null) {
            ordersView = new RequestsViewImpl(this);
        }
        return ordersView;
    }

    @Override
    public ProductResponseView getProductResponseView() {
        if(productResponseView == null) {
            productResponseView = new ProductResponseViewImpl(this);
        }
        return productResponseView;
    }

    @Override
    public ImageryResponseView getImageryResponseView() {
        if(imageryResponseView == null) {
            imageryResponseView = new ImageryResponseViewImpl(this);
        }
        return imageryResponseView;
    }

    @Override
    public ImagesResponseView getImagesResponseView() {
        if(imagesResponseView == null) {
            imagesResponseView = new ImagesResponseViewImpl(this);
        }
        return imagesResponseView;
    }

    @Override
    public ConversationView getConversationView() {
        if(conversationView == null) {
            conversationView = new ConversationViewImpl(this);
        }
        return conversationView;
    }

    @Override
    public VisualisationView getVisualisationView() {
        if(visualisationView == null) {
            visualisationView = new VisualisationViewImpl(this);
        }
        return visualisationView;
    }

}

