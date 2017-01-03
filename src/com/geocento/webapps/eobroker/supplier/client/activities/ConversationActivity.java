package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.places.ConversationPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.ConversationView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ConversationDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.MessageDTO;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class ConversationActivity extends TemplateActivity implements ConversationView.Presenter {

    private ConversationView conversationView;

    private ConversationDTO conversationDTO;

    public ConversationActivity(ConversationPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        conversationView = clientFactory.getConversationView();
        conversationView.setPresenter(this);
        panel.setWidget(conversationView.asWidget());
        setTemplateView(conversationView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {

        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        if(tokens.containsKey(ConversationPlace.TOKENS.id.toString())) {
            displayFullLoading("Loading conversation...");
            String conversationid = tokens.get(ConversationPlace.TOKENS.id.toString());
            try {
                REST.withCallback(new MethodCallback<ConversationDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideFullLoading();
                        Window.alert("Error loading conversation");
                    }

                    @Override
                    public void onSuccess(Method method, ConversationDTO conversationDTO) {
                        hideFullLoading();
                        ConversationActivity.this.conversationDTO = conversationDTO;
                        conversationView.displayConversation(conversationDTO);
                    }
                }).call(ServicesUtil.ordersService).getConversation(conversationid);
            } catch (Exception e) {

            }
            return;
        } else {
            Window.alert("Missing conversation id");
        }
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(conversationView.getSubmitMessage().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                displayLoading("Saving message");
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
                            conversationView.getMessageText().setText("");
                        }
                    }).call(ServicesUtil.ordersService).addConversationMessage(conversationDTO.getId(), conversationView.getMessageText().getText());
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));

    }

    private void addMessage(MessageDTO messageDTO) {
        String userName = Supplier.getLoginInfo().getUserName();
        boolean isCustomer = !userName.contentEquals(messageDTO.getFrom());
        conversationDTO.getMessages().add(messageDTO);
        conversationView.addMessage(messageDTO.getFrom(),
                isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
    }

}
