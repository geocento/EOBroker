package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.events.LogOut;
import com.geocento.webapps.eobroker.supplier.client.events.LogOutHandler;
import com.geocento.webapps.eobroker.supplier.client.events.SupplierNotifications;
import com.geocento.webapps.eobroker.supplier.client.events.SupplierNotificationsHandler;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.utils.NotificationMonitoring;
import com.geocento.webapps.eobroker.supplier.client.views.TemplateView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierNotificationDTO;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 29/06/2016.
 */
public abstract class TemplateActivity extends AbstractApplicationActivity {

    private TemplateView templateView;

    public TemplateActivity(ClientFactory clientFactory) {
        super(clientFactory);
        NotificationMonitoring.start(clientFactory);
    }

    public void setTemplateView(TemplateView templateView) {
        this.templateView = templateView;
        // update notifications if any
        displayNotifications(NotificationMonitoring.getNotifications());
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

        activityEventBus.addHandler(SupplierNotifications.TYPE, new SupplierNotificationsHandler() {
            @Override
            public void onSupplierNotifications(SupplierNotifications event) {
                displayNotifications(event.getSupplierNotifications());
            }
        });
    }

    private void displayNotifications(List<SupplierNotificationDTO> supplierNotifications) {
        if(templateView != null) {
            templateView.displayNotifications(supplierNotifications);
        }
    }
}
