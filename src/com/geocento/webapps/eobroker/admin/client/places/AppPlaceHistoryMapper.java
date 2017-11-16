package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({LoginPagePlace.Tokenizer.class,
        DashboardPlace.Tokenizer.class,
        UsersPlace.Tokenizer.class,
        CompaniesPlace.Tokenizer.class,
        CompanyPlace.Tokenizer.class,
        ProductsPlace.Tokenizer.class,
        ProductPlace.Tokenizer.class,
        ChallengesPlace.Tokenizer.class,
        ChallengePlace.Tokenizer.class,
        NewsItemsPlace.Tokenizer.class,
        NewsItemPlace.Tokenizer.class,
        FeedbackPlace.Tokenizer.class,
        SettingsPlace.Tokenizer.class,
        LogsPlace.Tokenizer.class
})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
