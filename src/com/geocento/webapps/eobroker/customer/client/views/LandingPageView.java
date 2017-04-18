package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.geocento.webapps.eobroker.customer.shared.Offer;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface LandingPageView extends IsWidget {

    void setPresenter(Presenter presenter);

    void displaySearchError(String message);

    void setNewsItems(List<NewsItem> newsItems);

    void setLoadingOffers(boolean loading);

    void setOffers(List<Offer> offers);

    public interface Presenter {
    }

}
