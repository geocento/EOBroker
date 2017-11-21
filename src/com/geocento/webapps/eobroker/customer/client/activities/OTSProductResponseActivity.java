package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.Notification;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.MessageEvent;
import com.geocento.webapps.eobroker.customer.client.events.MessageEventHandler;
import com.geocento.webapps.eobroker.customer.client.events.NotificationEvent;
import com.geocento.webapps.eobroker.customer.client.events.NotificationEventHandler;
import com.geocento.webapps.eobroker.customer.client.places.OTSProductResponsePlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.OTSProductResponseView;
import com.geocento.webapps.eobroker.customer.shared.NotificationDTO;
import com.geocento.webapps.eobroker.customer.shared.WebSocketMessage;
import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.OTSProductResponseDTO;
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
public class OTSProductResponseActivity extends TemplateActivity implements OTSProductResponseView.Presenter {

    private OTSProductResponseView otsProductResponseView;

    private OTSProductResponseDTO request;

    public OTSProductResponseActivity(OTSProductResponsePlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        otsProductResponseView = clientFactory.getOTSProductResponseView();
        otsProductResponseView.setPresenter(this);
        setTemplateView(otsProductResponseView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        displayFullLoading("Loading map...");
        otsProductResponseView.setMapLoadedHandler(new Callback<Void, Exception>() {
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

        String id = tokens.get(OTSProductResponsePlace.TOKENS.id.toString());
        if(id == null) {
            Window.alert("Request id cannot be null");
            History.back();
        }
        loadOTSProductResponse(id);
    }

    private void loadOTSProductResponse(String id) {
        try {
            displayFullLoading("Loading response...");
            REST.withCallback(new MethodCallback<OTSProductResponseDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideFullLoading();
                    Window.alert("Failed to load response");
                }

                @Override
                public void onSuccess(Method method, OTSProductResponseDTO otsProductResponseDTO) {
                    hideFullLoading();
                    request = otsProductResponseDTO;
                    otsProductResponseView.displayProductResponse(otsProductResponseDTO);
                }
            }).call(ServicesUtil.requestsService).getOTSProductResponse(id);
        } catch (Exception e) {

        }
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(otsProductResponseView.getSubmitMessage().addClickHandler(new ClickHandler() {
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
                            otsProductResponseView.getMessageText().setText("");
                        }
                    }).call(ServicesUtil.requestsService).addOTSProductResponseMessage(request.getId(), otsProductResponseView.getMessageText().getText());
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));

        activityEventBus.addHandler(MessageEvent.TYPE, new MessageEventHandler() {
            @Override
            public void onMessage(MessageEvent event) {
                // check this is a relevant message
                if (event.getType() == WebSocketMessage.TYPE.requestMessage) {
                    if((request.getId() + "").contentEquals(event.getDestination())) {
                        request.getMessages().add(event.getMessage());
                        addMessage(event.getMessage());
                    }
                } else if(event.getType() == WebSocketMessage.TYPE.otsproductResponse) {
                    if(event.getDestination().contentEquals(request.getId())) {
                        if(Window.confirm("New response on this request, reload page?")) {
                            loadOTSProductResponse(request.getId());
                        }
                    }
                }
            }
        });

        // check notification is not a new response
        activityEventBus.addHandler(NotificationEvent.TYPE, new NotificationEventHandler() {
            @Override
            public void onNotification(NotificationEvent event) {
                NotificationDTO notificationDTO = event.getNotification();
                // TODO - change so that it handles product request response changes as well as product request messages
                if(notificationDTO.getType() == Notification.TYPE.OTSPRODUCTREQUEST) {

                }
            }
        });


    }

    private void addMessage(MessageDTO messageDTO) {
        String userName = Customer.getLoginInfo().getUserName();
        boolean isCustomer = !userName.contentEquals(messageDTO.getFrom());
        otsProductResponseView.addMessage(messageDTO.getFrom(),
                isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
    }

}
