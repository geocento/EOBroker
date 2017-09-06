package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.*;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {

    private ClientFactory clientFactory;

    public AppActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
    	if (place instanceof LandingPagePlace) {
            return new LandingPageActivity((LandingPagePlace) place, clientFactory);
        } else if(place instanceof SearchPagePlace) {
            return new SearchPageActivity((SearchPagePlace) place, clientFactory);
        } else if(place instanceof ImageSearchPlace) {
            return new ImageSearchActivity((ImageSearchPlace) place, clientFactory);
        } else if(place instanceof CatalogueSearchPlace) {
            return new CatalogueSearchActivity((CatalogueSearchPlace) place, clientFactory);
        } else if(place instanceof RequestImageryPlace) {
            return new RequestImageryActivity((RequestImageryPlace) place, clientFactory);
        } else if(place instanceof ProductFormPlace) {
            return new ProductFormActivity((ProductFormPlace) place, clientFactory);
        } else if(place instanceof FullViewPlace) {
            return new FullViewActivity((FullViewPlace) place, clientFactory);
        } else if(place instanceof ProductFeasibilityPlace) {
            return new ProductFeasibilityActivity((ProductFeasibilityPlace) place, clientFactory);
        } else if(place instanceof RequestsPlace) {
            return new RequestsActivity((RequestsPlace) place, clientFactory);
        } else if(place instanceof ProductResponsePlace) {
            return new ProductResponseActivity((ProductResponsePlace) place, clientFactory);
        } else if(place instanceof ImageryResponsePlace) {
            return new ImageryResponseActivity((ImageryResponsePlace) place, clientFactory);
        } else if(place instanceof ImagesResponsePlace) {
            return new ImagesResponseActivity((ImagesResponsePlace) place, clientFactory);
        } else if(place instanceof VisualisationPlace) {
            return new VisualisationActivity((VisualisationPlace) place, clientFactory);
        } else if(place instanceof ConversationPlace) {
            return new ConversationActivity((ConversationPlace) place, clientFactory);
        } else if(place instanceof FeedbackPlace) {
            return new FeedbackActivity((FeedbackPlace) place, clientFactory);
        } else if(place instanceof SettingsPlace) {
            return new SettingsActivity((SettingsPlace) place, clientFactory);
        } else if(place instanceof CompanyPlace) {
            return new CompanyActivity((CompanyPlace) place, clientFactory);
        } else if(place instanceof TestimonialsPlace) {
            return new TestimonialsActivity((TestimonialsPlace) place, clientFactory);
        } else if(place instanceof TestimonialPlace) {
            return new TestimonialActivity((TestimonialPlace) place, clientFactory);
        } else if(place instanceof NotificationsPlace) {
            return new NotificationsActivity((NotificationsPlace) place, clientFactory);
        } else if(place instanceof LoginPagePlace) {
            return new LoginPageActivity((LoginPagePlace) place, clientFactory);
        } else if(place instanceof RequestAccessPlace) {
            return new RequestAccessActivity((RequestAccessPlace) place, clientFactory);
        }
        return null;
    }

}
