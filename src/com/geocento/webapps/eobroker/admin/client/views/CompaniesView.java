package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface CompaniesView extends IsWidget {

    void setPresenter(Presenter presenter);

    HasClickHandlers getCreateNewButton();

    void clearCompanies();

    void setCompaniesLoading(boolean loading);

    void addCompanies(boolean hasMore, List<CompanyDTO> response);

    TemplateView getTemplateView();

    public interface Presenter {
        void loadMore();

        void changeFilter(String value);
    }

}
