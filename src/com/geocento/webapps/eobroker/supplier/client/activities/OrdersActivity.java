package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.OrdersPlace;
import com.geocento.webapps.eobroker.supplier.client.views.OrdersView;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class OrdersActivity extends AbstractApplicationActivity implements OrdersView.Presenter {

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
        panel.setWidget(ordersView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
    }

    @Override
    protected void bind() {
    }

}
