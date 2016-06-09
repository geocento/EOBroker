package com.geocento.webapps.eobroker.client.activities;

import com.geocento.webapps.eobroker.client.ClientFactory;
import com.geocento.webapps.eobroker.client.places.LoginPagePlace;
import com.geocento.webapps.eobroker.client.views.LoginPageView;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public class LoginPageActivity extends AbstractApplicationActivity implements LoginPageView.Presenter {

    private LoginPageView loginPageView;

    public LoginPageActivity(LoginPagePlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        loginPageView = clientFactory.getLoginPageView();
        loginPageView.setPresenter(this);
        panel.setWidget(loginPageView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
    }

    @Override
    protected void bind() {

    }

}
