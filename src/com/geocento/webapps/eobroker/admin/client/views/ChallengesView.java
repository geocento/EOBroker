package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.shared.dtos.ChallengeDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ChallengesView extends IsWidget {

    void setPresenter(Presenter presenter);

    HasClickHandlers getCreateNewButton();

    void clearChallenges();

    void setChallengesLoading(boolean loading);

    void addChallenges(boolean hasMore, List<ChallengeDTO> response);

    HasClickHandlers getImportCSV();

    TemplateView getTemplateView();

    public interface Presenter {
        void loadMore();

        void changeFilter(String value);

        void reload();
    }

}
