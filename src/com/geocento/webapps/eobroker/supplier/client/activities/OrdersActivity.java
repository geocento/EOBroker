package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.OrdersPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.OrdersView;
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
public class OrdersActivity extends TemplateActivity implements OrdersView.Presenter {

    private OrdersView ordersView;

    public OrdersActivity(OrdersPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        ordersView = clientFactory.getOrdersView();
        ordersView.setPresenter(this);
        setTemplateView(ordersView.getTemplateView());
        panel.setWidget(ordersView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        // load all current requests
        try {
            REST.withCallback(new MethodCallback<List<RequestDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Failed to load your requests");
                }

                @Override
                public void onSuccess(Method method, List<RequestDTO> requestDTOs) {
                    ordersView.setRequests(requestDTOs);
                }
            }).call(ServicesUtil.ordersService).getRequests();
        } catch (RequestException e) {
        }
    }

    @Override
    protected void bind() {
        super.bind();
    }

}
