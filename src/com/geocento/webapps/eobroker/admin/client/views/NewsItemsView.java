package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface NewsItemsView extends IsWidget {

    void setPresenter(Presenter presenter);

    void addNewsItems(boolean hasMore, List<NewsItem> newsItems);

    HasClickHandlers getCreateNewsItemButton();

    void clearNewsItems();

    void setNewsItemsLoading(boolean loading);

    TemplateView getTemplateView();

    public interface Presenter {
        void loadMore();

        void changeFilter(String value);
    }

}
