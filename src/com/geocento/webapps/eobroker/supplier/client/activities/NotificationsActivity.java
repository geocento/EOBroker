package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.NotificationsPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.utils.NotificationHelper;
import com.geocento.webapps.eobroker.supplier.client.views.DashboardView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierNotificationDTO;
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
public class NotificationsActivity extends TemplateActivity implements DashboardView.Presenter {

    private DashboardView dashboardView;

    public NotificationsActivity(NotificationsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        dashboardView = clientFactory.getDashboardView();
        dashboardView.setPresenter(this);
        setTemplateView(dashboardView.getTemplateView());
        panel.setWidget(dashboardView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {

        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        // if id was provided, go directly to notification
        if(tokens.containsKey(NotificationsPlace.TOKENS.id.toString())) {
            // no need for company id
            displayFullLoading("Loading notification...");
            try {
                REST.withCallback(new MethodCallback<SupplierNotificationDTO>() {

                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideFullLoading();
                        Window.alert("Problem loading notification");
                        // could not find notification so load all notifications instead
                        clientFactory.getPlaceController().goTo(new NotificationsPlace());
                    }

                    @Override
                    public void onSuccess(Method method, SupplierNotificationDTO supplierNotificationDTO) {
                        hideFullLoading();
                        clientFactory.getPlaceController().goTo(NotificationHelper.convertToPlace(supplierNotificationDTO));
                    }

                }).call(ServicesUtil.assetsService).getNotification(Long.parseLong(tokens.get(NotificationsPlace.TOKENS.id.toString())));
            } catch (RequestException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            displayFullLoading("Loading notifications...");
            REST.withCallback(new MethodCallback<List<SupplierNotificationDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideFullLoading();
                    Window.alert("Error loading notifications...");
                }

                @Override
                public void onSuccess(Method method, List<SupplierNotificationDTO> response) {
                    hideFullLoading();
                    dashboardView.displayNotifications(response);
                }

            }).call(ServicesUtil.assetsService).getNotifications();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void bind() {
        super.bind();

    }

}
