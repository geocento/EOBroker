package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.ConversationPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.ConversationView;
import com.geocento.webapps.eobroker.customer.shared.ConversationDTO;
import com.geocento.webapps.eobroker.customer.shared.CreateConversationDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

        if(tokens.containsKey(ConversationPlace.TOKENS.conversationid.toString())) {
            String conversationid = tokens.get(ConversationPlace.TOKENS.conversationid.toString());
            try {
                REST.withCallback(new MethodCallback<ConversationDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        conversationView.displayConversationError("Conversation could not be loaded...");
                    }

                    @Override
                    public void onSuccess(Method method, ConversationDTO conversationDTO) {
                        ConversationActivity.this.conversationDTO = conversationDTO;
                        conversationView.displayConversation(conversationDTO);
                        loadPreviousConversations(conversationDTO.getCompany().getId(), conversationDTO.getId());
                    }
                }).call(ServicesUtil.requestsService).getConversation(conversationid);
            } catch (Exception e) {
            }
        } else {
            final String topic = tokens.get(ConversationPlace.TOKENS.topic.toString());
            String companyId = tokens.get(ConversationPlace.TOKENS.companyid.toString());
            if (topic != null && companyId != null) {
                try {
                    REST.withCallback(new MethodCallback<CompanyDTO>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            conversationView.displayConversationError("Conversation could not be created...");
                        }

                        @Override
                        public void onSuccess(Method method, CompanyDTO companyDTO) {
                            conversationDTO = new ConversationDTO();
                            conversationDTO.setTopic(topic);
                            conversationDTO.setCompany(companyDTO);
                            conversationDTO.setMessages(new ArrayList<MessageDTO>());
                            conversationDTO.setCreationDate(new Date());
                            conversationView.displayConversation(conversationDTO);
                            loadPreviousConversations(companyDTO.getId(), conversationDTO.getId());
                        }
                    }).call(ServicesUtil.assetsService).getCompany(Long.parseLong(companyId));
                } catch (Exception e) {
                }
            }
        }
    }

    private void loadPreviousConversations(Long companyId, final String conversationId) {
        // now load previous conversations
        try {
            REST.withCallback(new MethodCallback<List<ConversationDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    conversationView.displayConversationsError("Conversations could not be loaded...");
                }

                @Override
                public void onSuccess(Method method, List<ConversationDTO> conversationDTOs) {
                    conversationView.displayConversations(ListUtil.filterValues(conversationDTOs, new ListUtil.CheckValue<ConversationDTO>() {
                        @Override
                        public boolean isValue(ConversationDTO value) {
                            return !value.getId().equals(conversationId);
                        }
                    }));
                }
            }).call(ServicesUtil.requestsService).listConversations(companyId);
        } catch (Exception e) {
        }
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(conversationView.getSubmitMessage().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(conversationDTO.getId() == null) {
                    CreateConversationDTO createConversationDTO = new CreateConversationDTO();
                    createConversationDTO.setTopic(conversationDTO.getTopic());
                    createConversationDTO.setCompanyId(conversationDTO.getCompany().getId());
                    try {
                        REST.withCallback(new MethodCallback<ConversationDTO>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                hideLoading();
                                displayError(exception.getMessage());
                            }

                            @Override
                            public void onSuccess(Method method, ConversationDTO response) {
                                hideLoading();
                                conversationDTO = response;
                                History.newItem(PlaceHistoryHelper.convertPlace(new ConversationPlace(Utils.generateTokens(ConversationPlace.TOKENS.conversationid.toString(), conversationDTO.getId() + ""))), false);
                                saveMessage();
                            }
                        }).call(ServicesUtil.requestsService).createConversation(createConversationDTO);
                    } catch (RequestException e) {
                        e.printStackTrace();
                    }
                } else {
                    saveMessage();
                }
            }
        }));

    }

    private void saveMessage() {
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
                    conversationView.getMessageText().setText("");
                }
            }).call(ServicesUtil.requestsService).addConversationMessage(conversationDTO.getId(), conversationView.getMessageText().getText());
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    private void addMessage(MessageDTO messageDTO) {
        String userName = Customer.getLoginInfo().getUserName();
        boolean isCustomer = !userName.contentEquals(messageDTO.getFrom());
        conversationDTO.getMessages().add(messageDTO);
        conversationView.addMessage(messageDTO.getFrom(),
                isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
    }

}
