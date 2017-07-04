package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.NotificationsPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.utils.NotificationHelper;
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

        // if id was provided, go directly to notification
        if(tokens.containsKey(NotificationsPlace.TOKENS.id.toString())) {
            // no need for company id
            displayFullLoading("Loading notification...");
            try {
                REST.withCallback(new MethodCallback<NotificationDTO>() {

                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideFullLoading();
                        Window.alert("Problem loading notification");
                        // could not find notification so load all notifications instead
                        clientFactory.getPlaceController().goTo(new NotificationsPlace());
                    }

                    @Override
                    public void onSuccess(Method method, NotificationDTO notificationDTO) {
                        hideFullLoading();
                        clientFactory.getPlaceController().goTo(NotificationHelper.createNotificationPlace(notificationDTO));
                    }

                }).call(ServicesUtil.assetsService).getNotification(Long.parseLong(tokens.get(NotificationsPlace.TOKENS.id.toString())));
            } catch (RequestException e) {
                e.printStackTrace();
            }
            return;
        }

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
