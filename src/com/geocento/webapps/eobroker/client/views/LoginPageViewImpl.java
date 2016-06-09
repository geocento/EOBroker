package com.geocento.webapps.eobroker.client.views;

import com.geocento.webapps.eobroker.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.client.styles.StyleResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.ImageType;
import gwt.material.design.client.ui.MaterialImage;

/**
 * Created by thomas on 09/05/2016.
 */
public class LoginPageViewImpl extends Composite implements LoginPageView {

    private Presenter presenter;

    interface LandingPageUiBinder extends UiBinder<Widget, LoginPageViewImpl> {
    }

    private static LandingPageUiBinder ourUiBinder = GWT.create(LandingPageUiBinder.class);

    @UiField(provided = true)
    MaterialImage logo;

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

}