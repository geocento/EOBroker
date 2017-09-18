package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.LoginInfo;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.ResetPasswordPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.ResetPasswordView;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class ResetPasswordActivity extends AbstractApplicationActivity implements ResetPasswordView.Presenter {

    private ResetPasswordView resetPasswordView;

    private String resetToken;

    private String userName;

    public ResetPasswordActivity(ResetPasswordPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        resetPasswordView = clientFactory.getResetPasswordView();
        resetPasswordView.setPresenter(this);
        panel.setWidget(resetPasswordView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        userName = tokens.get(ResetPasswordPlace.TOKENS.username.toString());
        resetToken = tokens.get(ResetPasswordPlace.TOKENS.resetToken.toString());
        if(resetToken != null) {
            // display the password change screen
            resetPasswordView.showPasswordChangeScreen(userName);
        } else {
            // we are in password reset request screen
            // display the password reset request screen
            resetPasswordView.showPasswordResetRequestScreen(userName);
        }

    }

    @Override
    protected void bind() {
        handlers.add(
                resetPasswordView.getResetPasswordButton().addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        resetPassword(resetPasswordView.getAccountName());
                    }

                }));

        handlers.add(
                resetPasswordView.getUpdatePasswordButton().addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        String password = resetPasswordView.getPassword().getText();
                        String confirmPassword = resetPasswordView.getPasswordConfirmation().getText();
                        try {
                            if(password.length() < 5) {
                                throw new Exception("The password is not valid");
                            }
                            if(!password.contentEquals(confirmPassword)) {
                                throw new Exception("The passwords are different");
                            }
                        } catch (Exception e) {
                            resetPasswordView.showChangePasswordError(e.getMessage());
                            return;
                        }
                        resetPasswordView.showChangePasswordLoading(true);
                        changePassword(userName, resetToken, password);
                    }
                }));

    }

    private void resetPassword(String accountName) {
        // check account name is valid
        if(accountName == null || accountName.length() < 5) {
            resetPasswordView.showResetPasswordError("Identifier is not valid, at least 5 characters are required.");
            return;
        }
        resetPasswordView.showResetPasswordLoading(true);
        // wait
        try {
            REST.withCallback(new MethodCallback<LoginInfo>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    resetPasswordView.showResetPasswordLoading(false);
                    resetPasswordView.showResetPasswordError("Could not request reset password, reason: " + method.getResponse().getText());
                }

                @Override
                public void onSuccess(Method method, LoginInfo response) {
                    resetPasswordView.showResetPasswordLoading(false);
                    resetPasswordView.showResetPasswordSuccess("A password reset request link has been sent to the email address you provided us. Please click on the link to reset your password.");
                }
            }).call(ServicesUtil.loginService).resetPassword(accountName);
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    protected void changePassword(String accountName, String resetToken, String password) {
        resetPasswordView.showChangePasswordLoading(true);
        try {
            REST.withCallback(new MethodCallback<LoginInfo>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    resetPasswordView.showChangePasswordLoading(false);
                    resetPasswordView.showChangePasswordError("Could not change the password, reason: " + method.getResponse().getText());
                }

                @Override
                public void onSuccess(Method method, LoginInfo response) {
                    resetPasswordView.showChangePasswordLoading(false);
                    resetPasswordView.showChangePasswordSuccess("Your password was changed successfully.");
                }
            }).call(ServicesUtil.loginService).changePassword(accountName, resetToken, password);
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

}
