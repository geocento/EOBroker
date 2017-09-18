package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialLoadingButton;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialMessage;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.places.LoginPagePlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.styles.StyleResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import gwt.material.design.client.constants.ImageType;
import gwt.material.design.client.ui.*;

/**
 * Created by thomas on 09/05/2016.
 */
public class ResetPasswordViewImpl extends Composite implements ResetPasswordView {

    private Presenter presenter;

    interface ResetPasswordUiBinder extends UiBinder<Widget, ResetPasswordViewImpl> {
    }

    private static ResetPasswordUiBinder ourUiBinder = GWT.create(ResetPasswordUiBinder.class);

    @UiField(provided = true)
    MaterialImage logo;
    @UiField
    MaterialTextBox userName;
    @UiField
    MaterialTextBox password;
    @UiField
    MaterialPanel resetPasswordPanel;
    @UiField
    MaterialMessage resetPasswordMessage;
    @UiField
    MaterialLoadingButton resetPassword;
    @UiField
    MaterialPanel changePasswordPanel;
    @UiField
    MaterialTextBox confirmPassword;
    @UiField
    MaterialMessage changePasswordMessage;
    @UiField
    MaterialLoadingButton changePassword;
    @UiField
    MaterialLabel userNameLabel;

    public ResetPasswordViewImpl(ClientFactoryImpl clientFactory) {

        logo = new MaterialImage(StyleResources.INSTANCE.logoEOBroker(), ImageType.CIRCLE);

        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HasText getUserName() {
        return userName;
    }

    @Override
    public HasText getPassword() {
        return password;
    }

    @Override
    public HasText getPasswordConfirmation() {
        return confirmPassword;
    }

    @Override
    public HasKeyPressHandlers getPasswordBox() {
        return password;
    }

    @Override
    public void showPasswordChangeScreen(String userName) {
        resetPasswordPanel.setVisible(false);
        changePasswordPanel.setVisible(true);
        userNameLabel.setText("User name: " + userName);
        password.setValue("");
        confirmPassword.setValue("");
        changePasswordMessage.setVisible(false);
    }

    @Override
    public void showPasswordResetRequestScreen(String userName) {
        resetPasswordPanel.setVisible(true);
        changePasswordPanel.setVisible(false);
        changePasswordPanel.removeFromParent();
        this.userName.setText(userName);
        resetPasswordMessage.setVisible(false);
    }

    @Override
    public HasClickHandlers getResetPasswordButton() {
        return resetPassword;
    }

    @Override
    public String getAccountName() {
        return userName.getText();
    }

    @Override
    public HasClickHandlers getUpdatePasswordButton() {
        return changePassword;
    }

    @Override
    public void showChangePasswordError(String message) {
        changePasswordMessage.setVisible(true);
        changePasswordMessage.displayErrorMessage(message);
    }

    @Override
    public void showChangePasswordLoading(boolean display) {
        changePassword.setLoading(display);
    }

    @Override
    public void showResetPasswordError(String message) {
        resetPasswordMessage.setVisible(true);
        resetPasswordMessage.displayErrorMessage(message);
    }

    @Override
    public void showResetPasswordLoading(boolean display) {
        resetPassword.setLoading(display);
    }

    @Override
    public void showResetPasswordSuccess(String message) {
        // clear the panel
        resetPasswordPanel.clear();
        resetPasswordPanel.add(new HTMLPanel("<div style=\"margin: 10px 0px;\"><p style='font-size: 1.4em; color: green;'>Success</p><p>Your request to reset your password was submitted successfully. Please check your email account and follow the instructions to change your new password.</p></div>"));
    }

    @Override
    public void showChangePasswordSuccess(String message) {
        // clear the panel
        changePasswordPanel.clear();
        changePasswordPanel.add(new HTMLPanel("<div style=\"margin: 10px 0px;\"><p style='font-size: 1.4em; color: green;'>Success</p>" +
                "<p>You can now <a href='#" + PlaceHistoryHelper.convertPlace(new LoginPagePlace()) + "'>log in</a> the EO Broker with your new password.</p></div>"));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}