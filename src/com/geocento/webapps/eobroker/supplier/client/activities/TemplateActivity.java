package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.events.LogOut;
import com.geocento.webapps.eobroker.supplier.client.events.LogOutHandler;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

/**
 * Created by thomas on 29/06/2016.
 */
public abstract class TemplateActivity extends AbstractApplicationActivity {

    public TemplateActivity(ClientFactory clientFactory) {
        super(clientFactory);
    }

    @Override
    protected void bind() {
        activityEventBus.addHandler(LogOut.TYPE, new LogOutHandler() {
            @Override
            public void onLogOut(LogOut event) {
                REST.withCallback(new MethodCallback<Void>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, Void response) {
                        History.newItem("", false);
                        Window.Location.reload();
                    }
                }).call(ServicesUtil.loginService).signout();
            }
        });
    }
}
