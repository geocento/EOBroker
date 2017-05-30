package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.NotificationsPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.NotificationsView;
import com.geocento.webapps.eobroker.customer.shared.NotificationDTO;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class NotificationsActivity extends TemplateActivity implements NotificationsView.Presenter {

    private NotificationsView notificationsView;

    public NotificationsActivity(NotificationsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        notificationsView = clientFactory.getNotificationsView();
        notificationsView.setPresenter(this);
        setTemplateView(notificationsView.asWidget());
        selectMenu("notifications");
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    @Override
    protected void bind() {
        super.bind();
    }

    private void handleHistory() {

        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        // no need for company id
        displayFullLoading("Loading notifications...");
        try {
            REST.withCallback(new MethodCallback<List<NotificationDTO>>() {

                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideFullLoading();
                    Window.alert("Problem loading notifications");
                }

                @Override
                public void onSuccess(Method method, List<NotificationDTO> notificationDTOs) {
                    hideFullLoading();
                    notificationsView.displayNotifications(notificationDTOs);
                }

            }).call(ServicesUtil.assetsService).listNotifications(0, 100);
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

}
