package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.RequestCreated;
import com.geocento.webapps.eobroker.customer.client.events.ImageOrderCreatedHandler;
import com.geocento.webapps.eobroker.customer.client.events.LogOut;
import com.geocento.webapps.eobroker.customer.client.events.LogOutHandler;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.TemplateView;
import com.geocento.webapps.eobroker.customer.shared.RequestDTO;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 29/06/2016.
 */
public abstract class TemplateActivity extends AbstractApplicationActivity {

    private List<RequestDTO> userRequests = null;

    private TemplateView templateView;

    public TemplateActivity(ClientFactory clientFactory) {
        super(clientFactory);
    }

    public void setTemplateView(final TemplateView templateView) {
        this.templateView = templateView;
        // check use is logged in
        if(Customer.isLoggedIn()) {
            templateView.displaySignedIn(true);
            // load notifications and orders
            if (userRequests == null) {
                loadUserOrders();
            } else {
                templateView.setRequests(userRequests);
            }
        } else {
            templateView.displaySignedIn(false);
        }
    }

    private void loadUserOrders() {
        try {
            REST.withCallback(new MethodCallback<List<RequestDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<RequestDTO> requestDTOs) {
                    userRequests = requestDTOs;
                    templateView.setRequests(requestDTOs);
                }
            }).call(ServicesUtil.orderService).getRequests();
        } catch (RequestException e) {
        }
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
                        Window.Location.reload();
                    }
                }).call(ServicesUtil.loginService).signout();
            }
        });

        activityEventBus.addHandler(RequestCreated.TYPE, new ImageOrderCreatedHandler() {
            @Override
            public void onImageOrderCreated(RequestCreated event) {
                loadUserOrders();
            }
        });
    }
}
