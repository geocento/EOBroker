package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.events.LogOut;
import com.geocento.webapps.eobroker.admin.client.events.LogOutHandler;
import com.geocento.webapps.eobroker.admin.client.events.NotificationEvent;
import com.geocento.webapps.eobroker.admin.client.events.NotificationEventHandler;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.TemplateView;
import com.geocento.webapps.eobroker.admin.shared.dtos.NotificationDTO;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 29/06/2016.
 */
public abstract class TemplateActivity extends AbstractApplicationActivity implements TemplateView.Presenter {

    private List<NotificationDTO> userNotifications = null;

    private TemplateView templateView;

    public TemplateActivity(ClientFactory clientFactory) {
        super(clientFactory);
    }

    public void setTemplateView(TemplateView templateView) {
        this.templateView = templateView;
        if(userNotifications == null) {
            loadUserNotifications();
        } else {
            templateView.setNotifications(userNotifications);
        }
        templateView.setPresenter(this);
        // make sure page scrolls to the top
        templateView.scrollToTop();
    }

    private void loadUserNotifications() {
        try {
            REST.withCallback(new MethodCallback<List<NotificationDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<NotificationDTO> notificationDTOs) {
                    userNotifications = notificationDTOs;
                    templateView.setNotifications(notificationDTOs);
                }
            }).call(ServicesUtil.assetsService).getNotifications();
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

        activityEventBus.addHandler(NotificationEvent.TYPE, new NotificationEventHandler() {
            @Override
            public void onNotification(NotificationEvent event) {
                // update notifications
                NotificationDTO notificationDTO = event.getNotification();
                // make sure it is not already included
                if(!userNotifications.contains(notificationDTO)) {
                    userNotifications.add(notificationDTO);
                    templateView.setNotifications(userNotifications);
                }
            }
        });

    }

    protected void displayLoading(String message) {
        templateView.setLoading(message);
    }

    protected void hideLoading() {
        templateView.hideLoading();
    }

    protected void displayError(String message) {
        templateView.displayError(message);
    }

    protected void displaySuccess(String message) {
        templateView.displaySuccess(message);
    }

}
