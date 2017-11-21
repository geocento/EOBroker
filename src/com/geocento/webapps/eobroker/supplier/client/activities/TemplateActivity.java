package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.LogOut;
import com.geocento.webapps.eobroker.supplier.client.events.LogOutHandler;
import com.geocento.webapps.eobroker.supplier.client.events.WebSocketFailedEvent;
import com.geocento.webapps.eobroker.supplier.client.events.WebSocketFailedEventHandler;
import com.geocento.webapps.eobroker.supplier.client.places.NotificationEvent;
import com.geocento.webapps.eobroker.supplier.client.places.NotificationEventHandler;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.TemplateView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierNotificationDTO;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by thomas on 29/06/2016.
 */
public abstract class TemplateActivity extends AbstractApplicationActivity {

    private TemplateView templateView;

    private List<SupplierNotificationDTO> notifications = null;

    public TemplateActivity(ClientFactory clientFactory) {
        super(clientFactory);
    }

    public void setTemplateView(TemplateView templateView) {
        this.templateView = templateView;
        // load notifications
        if(notifications == null) {
            loadUserNotifications();
        } else {
            setNotifications(notifications);
        }
        templateView.setCompany(Supplier.getLoginInfo().getCompanyDTO());
        // make sure page scrolls to the top
        templateView.scrollToTop();
    }

    @Override
    protected void bind() {
        activityEventBus.addHandler(LogOut.TYPE, new LogOutHandler() {
            @Override
            public void onLogOut(LogOut event) {
                REST.withCallback(new MethodCallback<Void>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        Window.alert("Could not sign out session, reason is " + method.getResponse().getText());
                    }

                    @Override
                    public void onSuccess(Method method, Void response) {
                        History.newItem("", false);
                        Window.Location.reload();
                    }
                }).call(ServicesUtil.loginService).signout();
            }
        });

        activityEventBus.addHandler(WebSocketFailedEvent.TYPE, new WebSocketFailedEventHandler() {
            @Override
            public void onWebSocketFailed(WebSocketFailedEvent event) {
                templateView.displayWebsocketError("Failed to connect with server...");
                // TODO - take specific action when
            }
        });

        activityEventBus.addHandler(NotificationEvent.TYPE, new NotificationEventHandler() {
            @Override
            public void onNotification(NotificationEvent event) {
                // update notifications
                SupplierNotificationDTO notificationDTO = event.getSupplierNotificationDTO();
                // make sure it is not already included
                if(!notifications.contains(notificationDTO)) {
                    notifications.add(notificationDTO);
                    setNotifications(notifications);
                }
            }
        });


    }

    private void setNotifications(List<SupplierNotificationDTO> notifications) {
        Collections.sort(notifications, new Comparator<SupplierNotificationDTO>() {
            @Override
            public int compare(SupplierNotificationDTO o1, SupplierNotificationDTO o2) {
                return o2.getCreationDate().compareTo(o1.getCreationDate());
            }
        });
        templateView.setNotifications(notifications);
    }

    private void loadUserNotifications() {
        try {
            REST.withCallback(new MethodCallback<List<SupplierNotificationDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<SupplierNotificationDTO> notificationDTOs) {
                    notifications = notificationDTOs;
                    setNotifications(notificationDTOs);
                }
            }).call(ServicesUtil.assetsService).getNotifications();
        } catch (RequestException e) {
        }
    }

    public void displayLoading(String message) {
        templateView.setLoading(message);
    }

    public void hideLoading() {
        templateView.hideLoading();
    }

    public void displayFullLoading(String message) {
        templateView.displayFullLoading(message);
    }

    public void hideFullLoading() {
        templateView.hideFullLoading();
    }

    public void displayError(String message) {
        templateView.displayError(message);
    }

    public void displaySuccess(String message) {
        templateView.displaySuccess(message);
    }

}
