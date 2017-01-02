package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.FeedbackPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.FeedbackView;
import com.geocento.webapps.eobroker.admin.shared.dtos.FeedbackDTO;
import com.geocento.webapps.eobroker.admin.shared.dtos.MessageDTO;
import com.geocento.webapps.eobroker.admin.shared.dtos.UserDTO;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
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

    @Override
    protected void bind() {
        super.bind();

        handlers.add(feedbackView.getSubmitMessage().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                saveMessage();
            }
        }));

    }

    private void handleHistory() {

        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        if(tokens.containsKey(FeedbackPlace.TOKENS.feedbackid.toString())) {
            feedbackView.displayFeedback(true);
            final String feedbackid = tokens.get(FeedbackPlace.TOKENS.feedbackid.toString());
            try {
                REST.withCallback(new MethodCallback<FeedbackDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        feedbackView.displayFeedbackError("Feedback could not be loaded...");
                    }

                    @Override
                    public void onSuccess(Method method, FeedbackDTO feedbackDTO) {
                        FeedbackActivity.this.feedbackDTO = feedbackDTO;
                        feedbackView.setFeedback(feedbackDTO);
                        // now load previous feedbacks
                        try {
                            REST.withCallback(new MethodCallback<List<FeedbackDTO>>() {
                                @Override
                                public void onFailure(Method method, Throwable exception) {
                                    feedbackView.displayFeedbacksError("Feedbacks could not be loaded...");
                                }

                                @Override
                                public void onSuccess(Method method, List<FeedbackDTO> feedbackDTOs) {
                                    feedbackView.displayPreviousFeedbacks(ListUtil.filterValues(feedbackDTOs, new ListUtil.CheckValue<FeedbackDTO>() {
                                        @Override
                                        public boolean isValue(FeedbackDTO value) {
                                            return !value.getId().equals(feedbackid);
                                        }
                                    }));
                                }
                            }).call(ServicesUtil.assetsService).listFeedbacks(feedbackDTO.getUserDTO().getName());
                        } catch (Exception e) {
                        }
                    }
                }).call(ServicesUtil.assetsService).getFeedback(feedbackid);
            } catch (Exception e) {
            }
        } else {
            feedbackView.displayFeedback(false);
            // load all existing feedbacks
            try {
                REST.withCallback(new MethodCallback<List<FeedbackDTO>>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        feedbackView.displayFeedbacksError("Feedbacks could not be loaded...");
                    }

                    @Override
                    public void onSuccess(Method method, List<FeedbackDTO> feedbackDTOs) {
                        feedbackView.displayLatestFeedbacks(feedbackDTOs);
                    }
                }).call(ServicesUtil.assetsService).listFeedbacks(null);
            } catch (Exception e) {
            }
        }
    }

    private void loadPreviousFeedbacks(UserDTO userDTO, final String feedbackId) {
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
            }).call(ServicesUtil.assetsService).addFeedbackMessage(feedbackDTO.getId(), feedbackView.getMessageText().getText());
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    private void addMessage(MessageDTO messageDTO) {
        boolean isCustomer = messageDTO.getFrom() != null;
        feedbackDTO.getMessages().add(messageDTO);
        feedbackView.addMessage(messageDTO.getFrom(),
                isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
    }

}
