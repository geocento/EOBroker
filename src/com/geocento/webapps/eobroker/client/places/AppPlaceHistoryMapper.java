package com.geocento.webapps.eobroker.client.places;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({LandingPagePlace.Tokenizer.class, SearchPagePlace.Tokenizer.class, ImageSearchPlace.Tokenizer.class, LoginPagePlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
