package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.places.FeedbackPlace;
import com.geocento.webapps.eobroker.admin.shared.dtos.FeedbackDTO;
import com.geocento.webapps.eobroker.admin.shared.dtos.MessageDTO;
import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.ProgressButton;
import com.geocento.webapps.eobroker.common.client.widgets.UserWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.bubble.MaterialBubble;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.Position;
import gwt.material.design.client.ui.*;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class FeedbackViewImpl extends Composite implements FeedbackView {

    interface DashboardViewUiBinder extends UiBinder<Widget, FeedbackViewImpl> {
    }

    private static DashboardViewUiBinder ourUiBinder = GWT.create(DashboardViewUiBinder.class);

    public static interface Style extends CssResource {
    }

    @UiField
    Style style;

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
    UserWidget userImage;
    @UiField
    MaterialLabel comment;
    @UiField
    MaterialRow previousFeedback;
    @UiField
    MaterialRow recentFeedback;
    @UiField
    HTMLPanel feedbacksPanel;
    @UiField
    HTMLPanel feedbackPanel;

    private Presenter presenter;

    public FeedbackViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        userImage.setUser(Admin.getLoginInfo().getUserName());
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
    public void setPresenter(FeedbackView.Presenter presenter) {

    }

    @Override
    public void displayFeedbackError(String message) {
        comment.setVisible(true);
        comment.setText(message);
    }

    @Override
    public void displayFeedbacksError(String message) {
        previousFeedback.clear();
        previousFeedback.add(new MaterialLabel(message));
    }

    @Override
    public void displayPreviousFeedbacks(List<FeedbackDTO> feedbackDTOs) {
        previousFeedback.clear();
        if(feedbackDTOs == null || feedbackDTOs.size() == 0) {
            previousFeedback.add(new MaterialLabel("No previous feedback provided..."));
        } else {
            for (final FeedbackDTO feedbackDTO : feedbackDTOs) {
                MaterialBubble materialBubble = new MaterialBubble();
                materialBubble.setBackgroundColor("white");
                materialBubble.setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
                materialBubble.setPosition(Position.LEFT);
                materialBubble.setMarginLeft(12);
                materialBubble.setWidth("50%");
                // add content
                MaterialButton materialButton = new MaterialButton(ButtonType.FLOATING);
                materialButton.setIconType(IconType.OPEN_IN_BROWSER);
                materialButton.setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
                materialButton.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        template.getClientFactory().getPlaceController().goTo(new FeedbackPlace(Utils.generateTokens(FeedbackPlace.TOKENS.feedbackid.toString(), feedbackDTO.getId())));
                    }
                });
                materialBubble.add(materialButton);
                MaterialLabel materialLabel = new MaterialLabel("Feedback '" + feedbackDTO.getTopic() + "'");
                materialLabel.setFontSize(1.2, com.google.gwt.dom.client.Style.Unit.EM);
                materialBubble.add(materialLabel);
                materialLabel = new MaterialLabel("Created on " + DateUtils.formatDateOnly(feedbackDTO.getCreationDate()));
                materialLabel.setFontSize(0.8, com.google.gwt.dom.client.Style.Unit.EM);
                materialBubble.add(materialLabel);
                previousFeedback.add(materialBubble);
            }
        }
    }

    @Override
    public void displayLatestFeedbacks(List<FeedbackDTO> feedbackDTOs) {
        title.setTitle("Recent feedback");
        recentFeedback.clear();
        if(feedbackDTOs == null || feedbackDTOs.size() == 0) {
            title.setDescription("No feedback provided so far...");
        } else {
            title.setDescription("Displaying your recent feedback");
            for (final FeedbackDTO feedbackDTO : feedbackDTOs) {
                MaterialBubble materialBubble = new MaterialBubble();
                materialBubble.setBackgroundColor("white");
                materialBubble.setFloat(com.google.gwt.dom.client.Style.Float.LEFT);
                materialBubble.setPosition(Position.LEFT);
                materialBubble.setMarginLeft(12);
                materialBubble.setWidth("50%");
                // add content
                MaterialButton materialButton = new MaterialButton(ButtonType.FLOATING);
                materialButton.setIconType(IconType.OPEN_IN_BROWSER);
                materialButton.setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
                materialButton.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        template.getClientFactory().getPlaceController().goTo(new FeedbackPlace(Utils.generateTokens(FeedbackPlace.TOKENS.feedbackid.toString(), feedbackDTO.getId())));
                    }
                });
                materialBubble.add(materialButton);
                MaterialLabel materialLabel = new MaterialLabel("Feedback '" + feedbackDTO.getTopic() + "' from user '" + feedbackDTO.getUserDTO().getName() + "'");
                materialLabel.setFontSize(1.2, com.google.gwt.dom.client.Style.Unit.EM);
                materialBubble.add(materialLabel);
                materialLabel = new MaterialLabel("Created on " + DateUtils.formatDateOnly(feedbackDTO.getCreationDate()));
                materialLabel.setFontSize(0.8, com.google.gwt.dom.client.Style.Unit.EM);
                materialBubble.add(materialLabel);
                recentFeedback.add(materialBubble);
            }
        }
    }

    @Override
    public void displayFeedback(boolean display) {
        feedbackPanel.setVisible(display);
        feedbacksPanel.setVisible(!display);
    }

    @Override
    public void setFeedback(FeedbackDTO feedbackDTO) {
        title.setTitle(feedbackDTO.getTopic());
        title.setDescription("Feedback on EO Broker, started on " + DateUtils.formatDateOnly(feedbackDTO.getCreationDate()));
        displayMessages(feedbackDTO.getMessages());
    }

    private void displayMessages(List<MessageDTO> messages) {
        this.messages.clear();
        this.message.setText("");
        String userName = Admin.getLoginInfo().getUserName();
        if(messages.size() == 0) {
            comment.setVisible(true);
            comment.setText("No messages yet...");
            message.setPlaceholder("Start the feedback...");
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
        String colour = "white";
        UserWidget userWidget = new UserWidget(userName);
        userWidget.setMarginTop(8);
        userWidget.setFloat(isCustomer ? com.google.gwt.dom.client.Style.Float.LEFT : com.google.gwt.dom.client.Style.Float.RIGHT);
        userWidget.setSize(40);
        materialRow.add(userWidget);
        MaterialBubble materialBubble = new MaterialBubble();
        materialBubble.setBackgroundColor(colour);
        materialBubble.setFloat(isCustomer ? com.google.gwt.dom.client.Style.Float.LEFT : com.google.gwt.dom.client.Style.Float.RIGHT);
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
        materialLabel.setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
        materialLabel.setFontSize(0.6, com.google.gwt.dom.client.Style.Unit.EM);
        materialBubble.add(materialLabel);
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