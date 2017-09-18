package com.geocento.webapps.eobroker.customer.client.views;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ResetPasswordView extends IsWidget {

    void setPresenter(Presenter presenter);

    HasText getUserName();

    HasText getPassword();

    HasText getPasswordConfirmation();

    HasKeyPressHandlers getPasswordBox();

    void showPasswordChangeScreen(String userName);

    void showPasswordResetRequestScreen(String userName);

    HasClickHandlers getResetPasswordButton();

    String getAccountName();

    HasClickHandlers getUpdatePasswordButton();

    void showChangePasswordError(String message);

    void showChangePasswordLoading(boolean display);

    void showResetPasswordError(String message);

    void showResetPasswordLoading(boolean display);

    void showResetPasswordSuccess(String message);

    void showChangePasswordSuccess(String message);

    public interface Presenter {
    }

}
