package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.ProgressButton;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ConversationDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.MessageDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.avatar.MaterialAvatar;
import gwt.material.design.addins.client.bubble.MaterialBubble;
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

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialTitle title;
    @UiField
    MaterialRow messages;
    @UiField
    ProgressButton submitMessage;
    @UiField
    MaterialTextArea message;
    @UiField
    MaterialColumn userImage;

    public ConversationViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        MaterialAvatar materialAvatar = new MaterialAvatar(Supplier.getLoginInfo().getUserName());
        materialAvatar.setWidth("100%");
        userImage.add(materialAvatar);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void setPresenter(Presenter presenter) {

    }

    @Override
    public void displayConversation(ConversationDTO conversationDTO) {
        title.setTitle("'" + conversationDTO.getTopic() + "'");
        title.setDescription("Conversation with '" + conversationDTO.getUser().getName() + "'" +
                ", started on " + DateUtils.formatDateOnly(conversationDTO.getCreationDate()));
        displayMessages(conversationDTO.getMessages());
    }

    private void displayMessages(List<MessageDTO> messages) {
        this.messages.clear();
        this.message.setText("");
        String userName = Supplier.getLoginInfo().getUserName();
        if(messages.size() == 0) {
            this.messages.add(new MaterialLabel("No messages yet..."));
            message.setPlaceholder("Start the conversation...");
        } else {
            for (MessageDTO messageDTO : messages) {
                boolean isCustomer = !userName.contentEquals(messageDTO.getFrom());
                addMessage(messageDTO.getFrom(),
                        isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
            }
            message.setPlaceholder("Reply...");
        }
    }

    @Override
    public void addMessage(String imageUrl, boolean isCustomer, String message, Date date) {
        MaterialRow materialRow = new MaterialRow();
        materialRow.setMarginBottom(0);
        messages.add(materialRow);
        String colour = isCustomer ? "blue accent-1" : "green accent-1";
        MaterialAvatar materialAvatar = new MaterialAvatar();
        materialAvatar.setBackgroundColor(colour);
        materialAvatar.setMarginTop(8);
        materialAvatar.setFloat(isCustomer ? Style.Float.LEFT : Style.Float.RIGHT);
        materialAvatar.setWidth("40px");
        materialAvatar.setHeight("40px");
        materialAvatar.setShadow(1);
        materialAvatar.setCircle(true);
        materialRow.add(materialAvatar);
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
        materialLabel.setText(DateUtils.dateTimeFormat.format(date));
        materialLabel.setFloat(Style.Float.RIGHT);
        materialLabel.setFontSize(0.6, Style.Unit.EM);
        materialBubble.add(materialLabel);
        materialAvatar.setName(imageUrl);
        materialAvatar.initialize();
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