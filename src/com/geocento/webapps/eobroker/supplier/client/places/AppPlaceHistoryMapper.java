package com.geocento.webapps.eobroker.supplier.client.places;

import com.geocento.webapps.eobroker.customer.client.places.TestimonialPlace;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({DashboardPlace.Tokenizer.class,
        LoginPagePlace.Tokenizer.class,
        CompanyPlace.Tokenizer.class,
        ProductServicePlace.Tokenizer.class,
        ProductServicesPlace.Tokenizer.class,
        OrdersPlace.Tokenizer.class,
        OrderPlace.Tokenizer.class,
        DatasetProviderPlace.Tokenizer.class,
        ProductDatasetPlace.Tokenizer.class,
        ProductDatasetsPlace.Tokenizer.class,
        SoftwarePlace.Tokenizer.class,
        SoftwaresPlace.Tokenizer.class,
        ProjectPlace.Tokenizer.class,
        ProjectsPlace.Tokenizer.class,
        ConversationPlace.Tokenizer.class,
        TestimonialsPlace.Tokenizer.class,
        SuccessStoriesPlace.Tokenizer.class,
        SuccessStoryPlace.Tokenizer.class,
        SettingsPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
