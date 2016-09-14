package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.OrderPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.OrderView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ImageryServiceRequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ImagesRequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceSupplierRequestDTO;
import com.google.gwt.core.client.Callback;
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
        orderView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {

            }

            @Override
            public void onSuccess(Void result) {
                handleHistory();
            }
        });
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        String orderId = tokens.get(OrderPlace.TOKENS.id.toString());
        String typeString = tokens.get(OrderPlace.TOKENS.type.toString());
        if(orderId == null || typeString == null) {
            Window.alert("Request id cannot be null");
            History.back();
        }

        try {
            RequestDTO.TYPE type = RequestDTO.TYPE.valueOf(typeString);
            switch(type) {
                case product:
                    REST.withCallback(new MethodCallback<ProductServiceSupplierRequestDTO>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {

                        }

                        @Override
                        public void onSuccess(Method method, ProductServiceSupplierRequestDTO response) {
                            orderView.displayTitle("Viewing product request '" + response.getId() + "'");
                            orderView.displayUser(response.getCustomer());
                            orderView.displayProductRequest(response);
                        }
                    }).call(ServicesUtil.ordersService).getProductRequest(orderId);
                    break;
                case imageservice:
                    REST.withCallback(new MethodCallback<ImageryServiceRequestDTO>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {

                        }

                        @Override
                        public void onSuccess(Method method, ImageryServiceRequestDTO response) {
                            orderView.displayTitle("Viewing product request '" + response.getId() + "'");
                            orderView.displayUser(response.getCustomer());
                            orderView.displayImageryRequest(response);
                        }
                    }).call(ServicesUtil.ordersService).getImageryRequest(orderId);
                    break;
                case image:
                    REST.withCallback(new MethodCallback<ImagesRequestDTO>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {

                        }

                        @Override
                        public void onSuccess(Method method, ImagesRequestDTO response) {
                            orderView.displayTitle("Viewing product request '" + response.getId() + "'");
                            orderView.displayUser(response.getCustomer());
                            orderView.displayImagesRequest(response);
                        }
                    }).call(ServicesUtil.ordersService).getImagesRequest(orderId);
                    break;
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void bind() {
        super.bind();
    }

}
