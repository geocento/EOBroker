package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.styles.StyleResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.ImageType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialCheckBox;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialTextBox;

/**
 * Created by thomas on 09/05/2016.
 */
public class LoginPageViewImpl extends Composite implements LoginPageView {

    private Presenter presenter;

    interface LoginPageUiBinder extends UiBinder<Widget, LoginPageViewImpl> {
    }

    private static LoginPageUiBinder ourUiBinder = GWT.create(LoginPageUiBinder.class);

    @UiField(provided = true)
    MaterialImage logo;
    @UiField
    MaterialTextBox userName;
    @UiField
    MaterialTextBox password;
    @UiField
    MaterialCheckBox keepLoggedIn;
    @UiField
    MaterialButton login;

    public LoginPageViewImpl(ClientFactoryImpl clientFactory) {

        logo = new MaterialImage(StyleResources.INSTANCE.logoEOBroker(), ImageType.CIRCLE);

        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public HasClickHandlers getLogin() {
        return login;
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
    public HasValue<Boolean> getKeepLoggedIn() {
        return keepLoggedIn;
    }

}