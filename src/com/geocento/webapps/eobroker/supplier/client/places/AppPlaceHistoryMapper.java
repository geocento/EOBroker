package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({DashboardPlace.Tokenizer.class, LoginPagePlace.Tokenizer.class, CompanyPlace.Tokenizer.class, ServicesPlace.Tokenizer.class, OrdersPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
