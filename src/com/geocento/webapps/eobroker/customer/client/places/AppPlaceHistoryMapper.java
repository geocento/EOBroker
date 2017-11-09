package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({LandingPagePlace.Tokenizer.class,
        SearchPagePlace.Tokenizer.class,
        ImageSearchPlace.Tokenizer.class,
        CatalogueSearchPlace.Tokenizer.class,
        RequestImageryPlace.Tokenizer.class,
        ProductFormPlace.Tokenizer.class,
        FullViewPlace.Tokenizer.class,
        ProductFeasibilityPlace.Tokenizer.class,
        RequestsPlace.Tokenizer.class,
        ProductResponsePlace.Tokenizer.class,
        ImageryResponsePlace.Tokenizer.class,
        ImagesResponsePlace.Tokenizer.class,
        VisualisationPlace.Tokenizer.class,
        ConversationPlace.Tokenizer.class,
        FeedbackPlace.Tokenizer.class,
        SettingsPlace.Tokenizer.class,
        CompanyPlace.Tokenizer.class,
        TestimonialsPlace.Tokenizer.class,
        TestimonialPlace.Tokenizer.class,
        NotificationsPlace.Tokenizer.class,
        SuccessStoryPlace.Tokenizer.class,
        LoginPagePlace.Tokenizer.class,
        ResetPasswordPlace.Tokenizer.class,
        RequestAccessPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
