package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.supplier.shared.dtos.ConversationDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ConversationView extends IsWidget {

    TemplateView getTemplateView();

    void addMessage(String imageUrl, boolean isCustomer, String message, Date date);

    HasClickHandlers getSubmitMessage();

    HasText getMessageText();

    void displayConversation(ConversationDTO conversationDTO);

    void setPresenter(Presenter presenter);

    public interface Presenter {
    }

}
