package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialMessage;
import com.geocento.webapps.eobroker.common.client.widgets.ProgressButton;
import com.geocento.webapps.eobroker.common.client.widgets.UserWidget;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.widgets.ConversationWidget;
import com.geocento.webapps.eobroker.customer.shared.ConversationDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.bubble.MaterialBubble;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.Position;
import gwt.material.design.client.ui.*;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ConversationViewImpl extends Composite implements ConversationView {

    interface RequestViewUiBinder extends UiBinder<Widget, ConversationViewImpl> {
    }

    private static RequestViewUiBinder ourUiBinder = GWT.create(RequestViewUiBinder.class);

    @UiField
    MaterialTitle title;
    @UiField
    MaterialRow messages;
    @UiField
    ProgressButton submitMessage;
    @UiField
    MaterialTextArea message;
    @UiField
    UserWidget userImage;
    @UiField
    MaterialLabel comment;
    @UiField
    MaterialRow previousConversations;
    @UiField
    MaterialPanel conversationStarter;
    @UiField
    MaterialLabel previousConversationsTitle;
    @UiField
    MaterialMessage conversationStatus;
    @UiField
    MaterialLabel supplierTyping;

    private ClientFactoryImpl clientFactory;

    private Timer supplierTypingTimer;

    public ConversationViewImpl(ClientFactoryImpl clientFactory) {

        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

        userImage.setUser(Customer.getLoginInfo().getUserName());
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void setPresenter(Presenter presenter) {

    }

    @Override
    public void displayConversationError(String message) {
        comment.setVisible(true);
        comment.setText(message);
    }

    @Override
    public void displayConversationsError(String message) {
        previousConversations.clear();
        previousConversations.add(new MaterialLabel(message));
    }

    @Override
    public void displayConversations(List<ConversationDTO> conversationDTOs) {
        previousConversations.clear();
        if(conversationDTOs == null || conversationDTOs.size() == 0) {
            previousConversations.add(new MaterialLabel("No previous conversations with this company..."));
        } else {
            MaterialRow materialRow = new MaterialRow();
            this.previousConversations.add(materialRow);
            for (final ConversationDTO conversationDTO : conversationDTOs) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
                ConversationWidget conversationWidget = new ConversationWidget(conversationDTO);
                materialColumn.add(conversationWidget);
                materialRow.add(materialColumn);
            }
        }
    }

    @Override
    public void displayConversationStarter(boolean display) {
        conversationStarter.setVisible(display);
    }

    @Override
    public void setPresenceStatus(Boolean isOnline) {
        if(isOnline == null) {
            conversationStatus.setVisible(false);
            return;
        }
        conversationStatus.setVisible(true);
        if(isOnline) {
            conversationStatus.displaySuccessMessage("Company is online and may be able to reply immediately...");
        } else {
            conversationStatus.displayWarningMessage("Company is offline, they will be notified of your message...");
        }
    }

    @Override
    public void setSupplierTyping(String message, int duration) {
        supplierTyping.setText(message);
        if(supplierTypingTimer != null) {
            supplierTypingTimer.cancel();
            supplierTypingTimer = null;
        }
        supplierTypingTimer = new Timer() {
            @Override
            public void run() {
                supplierTyping.setText("");
                supplierTypingTimer = null;
            }
        };
        supplierTypingTimer.schedule(duration);
    }

    @Override
    public void setPreviousConversationTitle(String title) {
        previousConversationsTitle.setText(title);
    }

    @Override
    public void displayConversation(ConversationDTO conversationDTO) {
        title.setTitle(conversationDTO.getTopic());
        title.setDescription("Conversation with '" + conversationDTO.getCompany().getName() + "'" +
                ", started on " + DateUtils.formatDateOnly(conversationDTO.getCreationDate()));
        displayMessages(conversationDTO.getMessages());
    }

    private void displayMessages(List<MessageDTO> messages) {
        this.messages.clear();
        this.message.setText("");
        String userName = Customer.getLoginInfo().getUserName();
        if(messages.size() == 0) {
            comment.setVisible(true);
            comment.setText("No messages yet...");
            message.setPlaceholder("Start the conversation...");
        } else {
            comment.setVisible(false);
            for (MessageDTO messageDTO : messages) {
                boolean isCustomer = !userName.contentEquals(messageDTO.getFrom());
                addMessage(messageDTO.getFrom(),
                        isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
            }
            message.setPlaceholder("Reply...");
        }
    }

    @Override
    public void addMessage(String userName, boolean isCustomer, String message, Date date) {
        comment.setVisible(false);
        MaterialRow materialRow = new MaterialRow();
        materialRow.setMarginBottom(0);
        messages.add(materialRow);
        Color colour = Color.WHITE;
        UserWidget userWidget = new UserWidget(userName);
        userWidget.setMarginTop(8);
        userWidget.setFloat(isCustomer ? Style.Float.LEFT : Style.Float.RIGHT);
        userWidget.setSize(40);
        materialRow.add(userWidget);
        MaterialBubble materialBubble = new MaterialBubble();
        materialBubble.setBackgroundColor(colour);
        materialBubble.setFloat(isCustomer ? Style.Float.LEFT : Style.Float.RIGHT);
        materialBubble.setPosition(isCustomer ? Position.LEFT : Position.RIGHT);
        if(isCustomer) {
            materialBubble.setMarginLeft(12);
        } else {
            materialBubble.setMarginRight(12);
        }
        materialBubble.setWidth("50%");
        materialRow.add(materialBubble);
        materialBubble.add(new MaterialLabel(message));
        MaterialLabel materialLabel = new MaterialLabel();
        materialLabel.setText(DateUtils.dateFormat.format(date));
        materialLabel.setFloat(Style.Float.RIGHT);
        materialLabel.setFontSize(0.6, Style.Unit.EM);
        materialBubble.add(materialLabel);
/*
        userWidget.setName(userName);
        userWidget.initialize();
*/
    }

    @Override
    public HasClickHandlers getSubmitMessage() {
        return submitMessage;
    }

    @Override
    public HasText getMessageText() {
        return message;
    }

}