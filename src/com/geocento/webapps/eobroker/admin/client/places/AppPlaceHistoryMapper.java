package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({LoginPagePlace.Tokenizer.class,
        DashboardPlace.Tokenizer.class,
        CompaniesPlace.Tokenizer.class,
        CompanyPlace.Tokenizer.class,
        ProductsPlace.Tokenizer.class,
        ProductPlace.Tokenizer.class,
        NewsItemsPlace.Tokenizer.class,
        NewsItemPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
