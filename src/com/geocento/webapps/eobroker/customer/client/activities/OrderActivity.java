package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.OrderPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.OrderView;
import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class OrderActivity extends TemplateActivity implements OrderView.Presenter {

    private OrderView orderView;

    public OrderActivity(OrderPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        orderView = clientFactory.getOrderView();
        orderView.setPresenter(this);
        panel.setWidget(orderView.asWidget());
        setTemplateView(orderView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        String requestId = tokens.get(OrderPlace.TOKENS.id.toString());
        String requestType = tokens.get(OrderPlace.TOKENS.type.toString());
        if(requestId == null || requestType == null) {
            Window.alert("Missing request information");
            History.back();
            return;
        }
        // load all current requests
        try {
            REST.withCallback(new MethodCallback<RequestDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Failed to load your requests");
                }

                @Override
                public void onSuccess(Method method, RequestDTO requestDTO) {
                    orderView.setRequest(requestDTO);
                }
            }).call(ServicesUtil.ordersService).getRequest(requestId);
        } catch (RequestException e) {
        }
    }

}
