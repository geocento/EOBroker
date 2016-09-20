package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.ProductResponsePlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.ProductResponseView;
import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceSupplierResponseDTO;
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
public class ProductResponseActivity extends TemplateActivity implements ProductResponseView.Presenter {

    private ProductResponseView productResponseView;

    private ProductServiceResponseDTO request;
    private ProductServiceSupplierResponseDTO selectedResponse;

    public ProductResponseActivity(ProductResponsePlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        productResponseView = clientFactory.getProductResponseView();
        productResponseView.setPresenter(this);
        panel.setWidget(productResponseView.asWidget());
        setTemplateView(productResponseView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        productResponseView.setMapLoadedHandler(new Callback<Void, Exception>() {
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

        String id = tokens.get(ProductResponsePlace.TOKENS.id.toString());
        if(id == null) {
            Window.alert("Request id cannot be null");
            History.back();
        }
        Long responseId = null;
        if(tokens.containsKey(ProductResponsePlace.TOKENS.responseid.toString())) {
            try {
                responseId = Long.valueOf(tokens.get(ProductResponsePlace.TOKENS.responseid.toString()));
            } catch (Exception e) {
            }
        }

        try {
            final Long finalResponseId = responseId;
            REST.withCallback(new MethodCallback<ProductServiceResponseDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, ProductServiceResponseDTO productServiceResponseDTO) {
                    request = productServiceResponseDTO;
                    productResponseView.displayTitle("Viewing your product request '" + productServiceResponseDTO.getId() + "'");
                    productResponseView.displayComment("See below your request and the suppliers' responses");
                    productResponseView.displayProductRequest(productServiceResponseDTO);
                    if(finalResponseId == null) {
                        selectedResponse = request.getSupplierResponses().get(0);
                    }
                    selectedResponse = ListUtil.findValue(productServiceResponseDTO.getSupplierResponses(), new ListUtil.CheckValue<ProductServiceSupplierResponseDTO>() {
                        @Override
                        public boolean isValue(ProductServiceSupplierResponseDTO value) {
                            return value.getId().longValue() == finalResponseId;
                        }
                    });
                    selectResponse(selectedResponse);
                }
            }).call(ServicesUtil.ordersService).getProductResponse(id);
        } catch (Exception e) {

        }
    }

    private void selectResponse(ProductServiceSupplierResponseDTO selectedResponse) {
        this.selectedResponse = selectedResponse;
        productResponseView.displayProductResponse(selectedResponse);
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(productResponseView.getSubmitMessage().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                displayLoading();
                try {
                    REST.withCallback(new MethodCallback<MessageDTO>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError(exception.getMessage());
                        }

                        @Override
                        public void onSuccess(Method method, MessageDTO response) {
                            hideLoading();
                            addMessage(response);
                            productResponseView.getMessageText().setText("");
                        }
                    }).call(ServicesUtil.ordersService).addProductResponseMessage(selectedResponse.getId(), productResponseView.getMessageText().getText());
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));

    }

    private void displayError(String message) {
        productResponseView.getTemplateView().displayError(message);
    }

    private void hideLoading() {
        productResponseView.getTemplateView().hideLoading();
    }

    private void displayLoading() {
        productResponseView.getTemplateView().displayLoading();;
    }

    private void addMessage(MessageDTO messageDTO) {
        String userName = Customer.getLoginInfo().getUserName();
        boolean isCustomer = !userName.contentEquals(messageDTO.getFrom());
        productResponseView.addMessage(messageDTO.getFrom(),
                isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
    }

    @Override
    public void responseSelected(ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO) {

    }
}
