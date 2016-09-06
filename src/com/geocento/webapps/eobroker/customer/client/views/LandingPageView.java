package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface LandingPageView extends IsWidget {

    void displayAoI(AoI aoi);

    void displayCategory(Category category);

    void setPresenter(Presenter presenter);

    void displayListSuggestions(List<Suggestion> mutate);

    void displaySearchError(String message);

    void setNewsItems(List<NewsItem> newsItems);

    TemplateView getTemplateView();

    void setSearchText(String text);

    public interface Presenter {

        void categoryChanged(Category category);

        void aoiChanged(AoI aoi);

        void textChanged(String text);
    }

}
