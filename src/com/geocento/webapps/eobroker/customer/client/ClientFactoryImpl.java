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

    private LoginPageView loginPageView = null;

    private ImageSearchView imageSearchView = null;

    private CatalogueSearchView catalogueSearchView = null;

    private RequestImageryView requestImageryView = null;

    private ProductFormView productFormView = null;

    private FullView fullView = null;

    private ProductFeasibilityView productFeasibilityView = null;

    private RequestsView ordersView = null;

    private ProductResponseView productResponseView = null;

    private ImageryResponseView imageryResponseView = null;

    private ImagesResponseView imagesResponseView = null;

    private VisualisationView visualisationView = null;

    private ConversationView conversationView = null;

    private FeedbackView feedbackView = null;

    private SettingsView settingsView = null;

    private TestimonialsView testimonialsView = null;

    private TestimonialView testimonialView = null;

    private NotificationsView notificationsView = null;

    private CompanyView companyView = null;

    private RequestAccessView requestAccessView = null;

    private TemplateView templateView = null;

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

    @Override
    public FeedbackView getFeedbackView() {
        if(feedbackView == null) {
            feedbackView = new FeedbackViewImpl(this);
        }
        return feedbackView;
    }

    @Override
    public SettingsView getSettingsView() {
        if(settingsView == null) {
            settingsView = new SettingsViewImpl(this);
        }
        return settingsView;
    }

    @Override
    public TestimonialsView getTestimonialsView() {
        if(testimonialsView == null) {
            testimonialsView = new TestimonialsViewImpl(this);
        }
        return testimonialsView;
    }

    @Override
    public TemplateView getTemplateView() {
        if(templateView == null) {
            templateView = new TemplateView(this);
        }
        return templateView;
    }

    @Override
    public TestimonialView getTestimonialView() {
        if(testimonialView == null) {
            testimonialView = new TestimonialViewImpl(this);
        }
        return testimonialView;
    }

    @Override
    public CatalogueSearchView getCatalogueSearchView() {
        if(catalogueSearchView == null) {
            catalogueSearchView = new CatalogueSearchViewImpl(this);
        }
        return catalogueSearchView;
    }

    @Override
    public NotificationsView getNotificationsView() {
        if(notificationsView == null) {
            notificationsView = new NotificationsViewImpl(this);
        }
        return notificationsView;
    }

    @Override
    public RequestAccessView getRequestAccessView() {
        if(requestAccessView == null) {
            requestAccessView = new RequestAccessViewImpl(this);
        }
        return requestAccessView;
    }

    @Override
    public CompanyView getCompanyView() {
        if(companyView == null) {
            companyView = new CompanyViewImpl(this);
        }
        return companyView;
    }
}

