package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.LoginPagePlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.LoginPageView;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.LoginInfo;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gwt.material.design.client.ui.MaterialToast;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;

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

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
    }

    @Override
    protected void bind() {
        handlers.add(loginPageView.getLogin().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                REST.withCallback(new MethodCallback<LoginInfo>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        MaterialToast.fireToast("Wrong combination of user name and password");
                    }

                    @Override
                    public void onSuccess(Method method, LoginInfo response) {
                        if (response == null) {
                            MaterialToast.fireToast("Wrong combination of user name and password");
                        } else {
                            Admin.setLoginInfo(response);
                            Place nextPlace = ((LoginPagePlace) place).getNextPlace();
                            clientFactory.getEventBus().fireEvent(new PlaceChangeEvent(nextPlace == null ? clientFactory.getDefaultPlace() : nextPlace));
                        }
                    }
                }).call(ServicesUtil.loginService).signin(loginPageView.getUserName().getText(), loginPageView.getPassword().getText());
            }
        }));
    }

}
