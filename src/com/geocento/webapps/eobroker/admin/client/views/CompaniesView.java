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

    void setCompanies(List<CompanyDTO> response);

    HasClickHandlers getCreateNewButton();

    public interface Presenter {
    }

}
