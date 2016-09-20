package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.ImageryResponsePlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.ImageryResponseView;
import com.geocento.webapps.eobroker.customer.shared.requests.ImageryResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ImagerySupplierResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;
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
public class ImageryResponseActivity extends TemplateActivity implements ImageryResponseView.Presenter {

    private ImageryResponseView imageryResponseView;

    private ImageryResponseDTO request;
    private ImagerySupplierResponseDTO selectedResponse;

    public ImageryResponseActivity(ImageryResponsePlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        imageryResponseView = clientFactory.getImageryResponseView();
        imageryResponseView.setPresenter(this);
        panel.setWidget(imageryResponseView.asWidget());
        setTemplateView(imageryResponseView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        imageryResponseView.setMapLoadedHandler(new Callback<Void, Exception>() {
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

        String id = tokens.get(ImageryResponsePlace.TOKENS.id.toString());
        if(id == null) {
            Window.alert("Request id cannot be null");
            History.back();
        }
        Long responseId = null;
        if(tokens.containsKey(ImageryResponsePlace.TOKENS.responseid.toString())) {
            try {
                responseId = Long.valueOf(tokens.get(ImageryResponsePlace.TOKENS.responseid.toString()));
            } catch (Exception e) {
            }
        }

        try {
            final Long finalResponseId = responseId;
            REST.withCallback(new MethodCallback<ImageryResponseDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, ImageryResponseDTO imageryResponseDTO) {
                    request = imageryResponseDTO;
                    imageryResponseView.displayTitle("Viewing your imagery request '" + imageryResponseDTO.getId() + "'");
                    imageryResponseView.displayComment("See below your request and the suppliers' responses");
                    imageryResponseView.displayImageryRequest(imageryResponseDTO);
                    if(finalResponseId == null) {
                        selectedResponse = request.getSupplierResponses().get(0);
                    } else {
                        selectedResponse = ListUtil.findValue(imageryResponseDTO.getSupplierResponses(), new ListUtil.CheckValue<ImagerySupplierResponseDTO>() {
                            @Override
                            public boolean isValue(ImagerySupplierResponseDTO value) {
                                return value.getId().longValue() == finalResponseId;
                            }
                        });
                    }
                    selectResponse(selectedResponse);
                }
            }).call(ServicesUtil.ordersService).getImageResponse(id);
        } catch (Exception e) {

        }
    }

    private void selectResponse(ImagerySupplierResponseDTO selectedResponse) {
        this.selectedResponse = selectedResponse;
        imageryResponseView.displayImageryResponse(selectedResponse);
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(imageryResponseView.getSubmitMessage().addClickHandler(new ClickHandler() {
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
                            imageryResponseView.getMessageText().setText("");
                        }
                    }).call(ServicesUtil.ordersService).addImageryResponseMessage(selectedResponse.getId(), imageryResponseView.getMessageText().getText());
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));

    }

    private void displayError(String message) {
        imageryResponseView.getTemplateView().displayError(message);
    }

    private void hideLoading() {
        imageryResponseView.getTemplateView().hideLoading();
    }

    private void displayLoading() {
        imageryResponseView.getTemplateView().displayLoading();;
    }

    private void addMessage(MessageDTO messageDTO) {
        selectedResponse.getMessages().add(messageDTO);
        String userName = Customer.getLoginInfo().getUserName();
        boolean isCustomer = !userName.contentEquals(messageDTO.getFrom());
        imageryResponseView.addMessage(messageDTO.getFrom(),
                isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
    }

    @Override
    public void responseSelected(ImagerySupplierResponseDTO imagerySupplierResponseDTO) {
        selectResponse(imagerySupplierResponseDTO);
    }
}
