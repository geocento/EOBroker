package com.geocento.webapps.eobroker.client.views;

import com.geocento.webapps.eobroker.shared.Suggestion;
import com.geocento.webapps.eobroker.shared.entities.AoI;
import com.geocento.webapps.eobroker.shared.entities.Category;
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

    void displayText(String text);

    void displaySearchError(String message);

    public interface Presenter {

        void categoryChanged(Category category);

        void aoiChanged(AoI aoi);

        void textChanged(String text);
    }

}
