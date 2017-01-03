package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.places.LoginPagePlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.LoginPageView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.LoginInfo;
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
public class LoginActivity extends AbstractApplicationActivity implements LoginPageView.Presenter {

    private LoginPageView loginPageView;

    public LoginActivity(LoginPagePlace place, ClientFactory clientFactory) {
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
                        if(response == null) {
                            MaterialToast.fireToast("Wrong combination of user name and password");
                        } else {
                            Supplier.setLoginInfo(response);
                            Place nextPlace = ((LoginPagePlace) place).getNextPlace();
                            clientFactory.getEventBus().fireEvent(new PlaceChangeEvent(nextPlace == null ? clientFactory.getDefaultPlace() : nextPlace));
                        }
                    }
                }).call(ServicesUtil.loginService).signin(loginPageView.getUserName().getText(), loginPageView.getPassword().getText());
            }
        }));
    }

}
