package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.geocento.webapps.eobroker.customer.shared.FollowingEventDTO;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface LandingPageView extends IsWidget {

    void setPresenter(Presenter presenter);

    void displaySearchError(String message);

    void setNewsItems(List<NewsItem> newsItems);

    void setLoadingFollowingEvents(boolean loading);

    void addNewsFollowingEvents(boolean hasMore, List<FollowingEventDTO> followingEventDTOs);

    void clearNewsFeed();

    void displayFollowingMessage(String message);

    void hideFollowingMessage();

    public interface Presenter {
        void loadMoreFollowingEvents();
    }

}
