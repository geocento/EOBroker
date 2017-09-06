package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface SettingsView extends IsWidget {

    void setPresenter(Presenter presenter);

    HasText getFullName();

    void setIconUrl(String userIconUrl);

    String getIconUrl();

    HasText getEmail();

    void setCompany(CompanyDTO companyDTO);

    HasClickHandlers getSaveButton();

    public interface Presenter {
    }

}
