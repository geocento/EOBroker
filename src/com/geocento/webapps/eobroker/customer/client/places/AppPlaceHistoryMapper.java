package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({LandingPagePlace.Tokenizer.class,
        SearchPagePlace.Tokenizer.class,
        ImageSearchPlace.Tokenizer.class,
        RequestImageryPlace.Tokenizer.class,
        ProductFormPlace.Tokenizer.class,
        FullViewPlace.Tokenizer.class,
        ProductFeasibilityPlace.Tokenizer.class,
        OrdersPlace.Tokenizer.class,
        OrderPlace.Tokenizer.class,
        LoginPagePlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
