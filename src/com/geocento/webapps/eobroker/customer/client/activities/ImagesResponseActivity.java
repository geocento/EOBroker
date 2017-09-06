package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.ChangeStatus;
import com.geocento.webapps.eobroker.customer.client.events.ChangeStatusHandler;
import com.geocento.webapps.eobroker.customer.client.places.ImagesResponsePlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.ImagesResponseView;
import com.geocento.webapps.eobroker.customer.shared.requests.ImagesServiceResponseDTO;
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
public class ImagesResponseActivity extends TemplateActivity implements ImagesResponseView.Presenter {

    private ImagesResponseView imagesResponseView;

    private ImagesServiceResponseDTO request;

    public ImagesResponseActivity(ImagesResponsePlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        imagesResponseView = clientFactory.getImagesResponseView();
        imagesResponseView.setPresenter(this);
        setTemplateView(imagesResponseView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        displayFullLoading("Loading map...");
        imagesResponseView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Failed to load map");
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

        String requestId = tokens.get(ImagesResponsePlace.TOKENS.id.toString());
        if(requestId == null) {
            Window.alert("Request id cannot be null");
            History.back();
        }

        loadResponse(requestId);
    }

    private void loadResponse(String requestId) {
        try {
            displayFullLoading("Loading response...");
            REST.withCallback(new MethodCallback<ImagesServiceResponseDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideFullLoading();
                    Window.alert("Failed to load response");
                }

                @Override
                public void onSuccess(Method method, ImagesServiceResponseDTO imagesServiceResponseDTO) {
                    hideFullLoading();
                    request = imagesServiceResponseDTO;
                    imagesResponseView.displayTitle("Viewing images request '" + imagesServiceResponseDTO.getId() + "'");
                    imagesResponseView.displayComment("See below your request and the supplier's response");
                    imagesResponseView.displayImagesRequest(imagesServiceResponseDTO);
                }
            }).call(ServicesUtil.requestsService).getImagesResponse(requestId);
        } catch (Exception e) {

        }
    }

    @Override
    protected void bind() {
        super.bind();

        activityEventBus.addHandler(ChangeStatus.TYPE, new ChangeStatusHandler() {
            @Override
            public void onChangeStatus(ChangeStatus event) {
                if (Window.confirm("Are you sure you want to change the status to '" + event.getStatus() + "'")) {
                    displayLoading();
                    try {
                        REST.withCallback(new MethodCallback<Void>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                hideLoading();
                                displayError(method.getResponse().getText());
                            }

                            @Override
                            public void onSuccess(Method method, Void response) {
                                hideLoading();
                                loadResponse(request.getId());
                            }
                        }).call(ServicesUtil.requestsService).updateRequestStatus(request.getId(), event.getStatus());
                    } catch (RequestException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        handlers.add(imagesResponseView.getSubmitMessage().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                displayLoading();
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
                            addMessage(response);
                            imagesResponseView.getMessageText().setText("");
                        }
                    }).call(ServicesUtil.requestsService).addImagesResponseMessage(request.getId(), imagesResponseView.getMessageText().getText());
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));

    }

    private void addMessage(MessageDTO messageDTO) {
        String userName = Customer.getLoginInfo().getUserName();
        boolean isCustomer = !userName.contentEquals(messageDTO.getFrom());
        imagesResponseView.addMessage(messageDTO.getFrom(),
                isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
    }

}
