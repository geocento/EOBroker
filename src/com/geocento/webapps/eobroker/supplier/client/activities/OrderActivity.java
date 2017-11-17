package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.requests.RequestDTO;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.MessageEvent;
import com.geocento.webapps.eobroker.supplier.client.events.MessageEventHandler;
import com.geocento.webapps.eobroker.supplier.client.places.OrderPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.OrderView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.*;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
    private RequestDTO.TYPE type;
    private BaseRequestDTO request;

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
        displayFullLoading("Loading map...");
        orderView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Problem loading map...");
                hideFullLoading();
                handleHistory();
            }

            @Override
            public void onSuccess(Void result) {
                hideFullLoading();
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
            displayFullLoading("Loading request...");
            type = RequestDTO.TYPE.valueOf(typeString);
            switch(type) {
                case product:
                    REST.withCallback(new MethodCallback<ProductServiceSupplierRequestDTO>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideFullLoading();
                            Window.alert("Problem loading request");
                        }

                        @Override
                        public void onSuccess(Method method, ProductServiceSupplierRequestDTO productServiceSupplierRequestDTO) {
                            hideFullLoading();
                            request = productServiceSupplierRequestDTO;
                            orderView.displayProductRequest(productServiceSupplierRequestDTO);
                        }
                    }).call(ServicesUtil.ordersService).getProductRequest(orderId);
                    break;
                case otsproduct:
                    REST.withCallback(new MethodCallback<OTSProductRequestDTO>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideFullLoading();
                            Window.alert("Problem loading request");
                        }

                        @Override
                        public void onSuccess(Method method, OTSProductRequestDTO otsProductRequestDTO) {
                            hideFullLoading();
                            request = otsProductRequestDTO;
                            orderView.displayOTSProductRequest(otsProductRequestDTO);
                        }
                    }).call(ServicesUtil.ordersService).getOTSProductRequest(orderId);
                    break;
                case imageservice:
                    REST.withCallback(new MethodCallback<ImageryServiceRequestDTO>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideFullLoading();
                            Window.alert("Problem loading request");
                        }

                        @Override
                        public void onSuccess(Method method, ImageryServiceRequestDTO imageryServiceRequestDTO) {
                            hideFullLoading();
                            request = imageryServiceRequestDTO;
                            orderView.displayTitle("Viewing imagery request '" + imageryServiceRequestDTO.getId() + "'");
                            orderView.displayUser(imageryServiceRequestDTO.getCustomer());
                            orderView.displayImageryRequest(imageryServiceRequestDTO);
                        }
                    }).call(ServicesUtil.ordersService).getImageryRequest(orderId);
                    break;
                case image:
                    REST.withCallback(new MethodCallback<ImagesRequestDTO>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideFullLoading();
                            Window.alert("Problem loading request");
                        }

                        @Override
                        public void onSuccess(Method method, ImagesRequestDTO imagesRequestDTO) {
                            hideFullLoading();
                            request = imagesRequestDTO;
                            orderView.displayTitle("Viewing images request '" + imagesRequestDTO.getId() + "'");
                            orderView.displayUser(imagesRequestDTO.getCustomer());
                            orderView.displayImagesRequest(imagesRequestDTO);
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

        handlers.add(orderView.getSubmitMessage().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                displayLoading("Saving message");
                try {
                    REST.withCallback(new MethodCallback<MessageDTO>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError(method.getResponse().getText());
                        }

                        @Override
                        public void onSuccess(Method method, MessageDTO response) {
                            hideLoading();
                            orderView.getMessageText().setText("");
                            addMessage(response);
                        }
                    }).call(ServicesUtil.ordersService).addRequestMessage(type, request.getId(), orderView.getMessageText().getText());
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));

        handlers.add(orderView.getSubmitResponse().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                displayLoading("Saving response");
                try {
                    final String response = orderView.getResponse();
                    REST.withCallback(new MethodCallback<Void>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError(method.getResponse().getText());
                        }

                        @Override
                        public void onSuccess(Method method, Void result) {
                            hideLoading();
                            orderView.displayResponse(response);
                        }
                    }).call(ServicesUtil.ordersService).submitRequestResponse(type, request.getId(), response);
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));

        activityEventBus.addHandler(MessageEvent.TYPE, new MessageEventHandler() {
            @Override
            public void onMessage(MessageEvent event) {
                // check this is a relevant message
                if (event.getType() == SupplierWebSocketMessage.TYPE.requestMessage) {
                    if(request instanceof ProductServiceSupplierRequestDTO) {
                        ProductServiceSupplierRequestDTO productServiceSupplierRequestDTO = (ProductServiceSupplierRequestDTO) request;
                        // check if it belongs to the response
                        if (productServiceSupplierRequestDTO.getSupplierRequestId().toString().equals(event.getDestination())) {
                            request.getMessages().add(event.getMessage());
                            addMessage(event.getMessage());
                        }
                    } else if(request instanceof OTSProductRequestDTO) {
                        OTSProductRequestDTO otsProductRequestDTO = (OTSProductRequestDTO) request;
                        // check if it belongs to the response
                        if (otsProductRequestDTO.getId().toString().equals(event.getDestination())) {
                            request.getMessages().add(event.getMessage());
                            addMessage(event.getMessage());
                        }
                    }
                }
            }
        });

    }

    private void addMessage(MessageDTO messageDTO) {
        String userName = Supplier.getLoginInfo().getUserName();
        boolean isCustomer = !userName.contentEquals(messageDTO.getFrom());
        orderView.addMessage(messageDTO.getFrom(),
                isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
    }

}
