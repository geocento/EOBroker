package com.geocento.webapps.eobroker.supplier.client.views;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface LoginPageView extends IsWidget {

    void setPresenter(Presenter presenter);

    HasClickHandlers getLogin();

    HasText getUserName();

    HasText getPassword();

    HasValue<Boolean> getKeepLoggedIn();

    HasKeyPressHandlers getPasswordBox();

    public interface Presenter {
    }

}
