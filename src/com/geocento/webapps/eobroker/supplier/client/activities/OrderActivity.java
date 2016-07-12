package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.OrderPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.OrderView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceSupplierRequestDTO;
import com.google.gwt.event.shared.EventBus;
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
        setTemplateView(orderView.getTemplateView());
        panel.setWidget(orderView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        String orderId = tokens.get(OrderPlace.TOKENS.id.toString());
        if(orderId == null) {
            Window.alert("Request id cannot be null");
            History.back();
        }

        try {
            REST.withCallback(new MethodCallback<ProductServiceSupplierRequestDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, ProductServiceSupplierRequestDTO response) {
                    orderView.displayTitle("Viewing product request '" + response.getId() + "'");
                    orderView.displayUser(response.getCustomer());
                    orderView.displayFormValue(response.getFormValues());
                }
            }).call(ServicesUtil.ordersService).getRequest(orderId);
        } catch (Exception e) {

        }
    }

    @Override
    protected void bind() {
        super.bind();
    }

}
