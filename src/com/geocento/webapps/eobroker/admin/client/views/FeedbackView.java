package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.shared.dtos.FeedbackDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface FeedbackView extends IsWidget {

    void setPresenter(Presenter presenter);

    TemplateView getTemplateView();

    void addMessage(String imageUrl, boolean isCustomer, String message, Date date);

    HasClickHandlers getSubmitMessage();

    HasText getMessageText();

    void displayFeedback(boolean display);

    void setFeedback(FeedbackDTO feedbackDTO);

    void displayFeedbackError(String message);

    void displayFeedbacksError(String message);

    void displayPreviousFeedbacks(List<FeedbackDTO> feedbackDTOs);

    void displayLatestFeedbacks(List<FeedbackDTO> feedbackDTOs);

    public interface Presenter {
    }

}
