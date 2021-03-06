package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.shared.FeedbackDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface FeedbackView extends IsWidget {

    void setFeedbackTitle(String title);

    void setFeedbackDescription(String description);

    void addMessage(String imageUrl, boolean isCustomer, String message, Date date);

    HasClickHandlers getSubmitMessage();

    HasText getMessageText();

    void displayFeedbackMessages(List<MessageDTO> feedbackDTO);

    void setPresenter(Presenter presenter);

    void displayFeedbackError(String message);

    void displayFeedbacksError(String message);

    void displayFeedbacks(List<FeedbackDTO> feedbackDTOs);

    public interface Presenter {
    }

}
