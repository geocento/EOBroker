package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface RequestAccessView extends IsWidget {

    void setPresenter(Presenter presenter);

    HasText getUserName();

    HasText getPassword();

    HasKeyPressHandlers getPasswordBox();

    HasText getFullName();

    HasText getEmail();

    HasText getPhoneNumber();

    CompanyDTO getCompany();

    HasText getCompanyName();

    HasText getCompanyDescription();

    HasText getCompanyAddress();

    String getCompanyCountryCode();

    HasValue<Boolean> isSupplier();

    HasClickHandlers getSubmit();

    void displayCompanyCreationError(String message);

    void displayUserCreationError(String message);

    void displayCreationSuccess(String message);

    void displayLoading(String message);

    void hideLoading();

    public interface Presenter {
    }

}
