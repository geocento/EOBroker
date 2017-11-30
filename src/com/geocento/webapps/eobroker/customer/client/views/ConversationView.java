package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.shared.ConversationDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ConversationView extends IsWidget {

    void addMessage(String imageUrl, boolean isCustomer, String message, Date date);

    HasClickHandlers getSubmitMessage();

    HasText getMessageText();

    void setPreviousConversationTitle(String title);

    void displayConversation(ConversationDTO conversationDTO);

    void setPresenter(Presenter presenter);

    void displayConversationError(String message);

    void displayConversationsError(String message);

    void displayConversations(List<ConversationDTO> conversationDTOs);

    void displayConversationStarter(boolean display);

    void setPresenceStatus(Boolean isOnline);

    public interface Presenter {
    }

}
