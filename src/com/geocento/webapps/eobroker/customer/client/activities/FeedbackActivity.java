package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.FeedbackPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.FeedbackView;
import com.geocento.webapps.eobroker.customer.shared.CreateFeedbackDTO;
import com.geocento.webapps.eobroker.customer.shared.FeedbackDTO;
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
public class FeedbackActivity extends TemplateActivity implements FeedbackView.Presenter {

    private FeedbackView feedbackView;

    private FeedbackDTO feedbackDTO;

    public FeedbackActivity(FeedbackPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        feedbackView = clientFactory.getFeedbackView();
        feedbackView.setPresenter(this);
        panel.setWidget(feedbackView.asWidget());
        setTemplateView(feedbackView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {

        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        if(tokens.containsKey(FeedbackPlace.TOKENS.feedbackid.toString())) {
            String feedbackid = tokens.get(FeedbackPlace.TOKENS.feedbackid.toString());
            try {
                REST.withCallback(new MethodCallback<FeedbackDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        feedbackView.displayFeedbackError("Feedback could not be loaded...");
                    }

                    @Override
                    public void onSuccess(Method method, FeedbackDTO feedbackDTO) {
                        FeedbackActivity.this.feedbackDTO = feedbackDTO;
                        feedbackView.displayFeedback(feedbackDTO);
                        loadPreviousFeedbacks(feedbackDTO.getId());
                    }
                }).call(ServicesUtil.requestsService).getFeedback(feedbackid);
            } catch (Exception e) {
            }
        } else {
            final String topic = tokens.get(FeedbackPlace.TOKENS.topic.toString());
            if (topic != null) {
                feedbackDTO = new FeedbackDTO();
                feedbackDTO.setTopic(topic);
                feedbackDTO.setMessages(new ArrayList<MessageDTO>());
                feedbackDTO.setCreationDate(new Date());
                feedbackView.displayFeedback(feedbackDTO);
                loadPreviousFeedbacks(null);
            }
        }
    }

    private void loadPreviousFeedbacks(final String feedbackId) {
        // now load previous feedbacks
        try {
            REST.withCallback(new MethodCallback<List<FeedbackDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    feedbackView.displayFeedbacksError("Feedbacks could not be loaded...");
                }

                @Override
                public void onSuccess(Method method, List<FeedbackDTO> feedbackDTOs) {
                    feedbackView.displayFeedbacks(ListUtil.filterValues(feedbackDTOs, new ListUtil.CheckValue<FeedbackDTO>() {
                        @Override
                        public boolean isValue(FeedbackDTO value) {
                            return !value.getId().equals(feedbackId);
                        }
                    }));
                }
            }).call(ServicesUtil.requestsService).listFeedbacks();
        } catch (Exception e) {
        }
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(feedbackView.getSubmitMessage().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(feedbackDTO.getId() == null) {
                    CreateFeedbackDTO createFeedbackDTO = new CreateFeedbackDTO();
                    createFeedbackDTO.setTopic(feedbackDTO.getTopic());
                    try {
                        REST.withCallback(new MethodCallback<FeedbackDTO>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                hideLoading();
                                displayError(exception.getMessage());
                            }

                            @Override
                            public void onSuccess(Method method, FeedbackDTO response) {
                                hideLoading();
                                feedbackDTO = response;
                                History.newItem(PlaceHistoryHelper.convertPlace(new FeedbackPlace(Utils.generateTokens(FeedbackPlace.TOKENS.feedbackid.toString(), feedbackDTO.getId() + ""))), false);
                                saveMessage();
                            }
                        }).call(ServicesUtil.requestsService).createFeedback(createFeedbackDTO);
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
                    feedbackView.getMessageText().setText("");
                }
            }).call(ServicesUtil.requestsService).addFeedbackMessage(feedbackDTO.getId(), feedbackView.getMessageText().getText());
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    private void addMessage(MessageDTO messageDTO) {
        String userName = Customer.getLoginInfo().getUserName();
        boolean isCustomer = !userName.contentEquals(messageDTO.getFrom());
        feedbackDTO.getMessages().add(messageDTO);
        feedbackView.addMessage(messageDTO.getFrom(),
                isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
    }

}
