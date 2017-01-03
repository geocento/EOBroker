package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.ProgressButton;
import com.geocento.webapps.eobroker.common.client.widgets.UserWidget;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.requests.Request;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.ChangeStatus;
import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import gwt.material.design.addins.client.bubble.MaterialBubble;
import gwt.material.design.client.constants.Position;
import gwt.material.design.client.constants.TextAlign;
import gwt.material.design.client.ui.*;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class RequestViewImpl extends Composite implements RequestView {

    interface RequestViewUiBinder extends UiBinder<Widget, RequestViewImpl> {
    }

    private static RequestViewUiBinder ourUiBinder = GWT.create(RequestViewUiBinder.class);

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialLabel title;
    @UiField
    protected
    MaterialColumn requestDescription;
    @UiField
    HTMLPanel requestResponse;
    @UiField
    MaterialRow messages;
    @UiField
    protected
    MapContainer mapContainer;
    @UiField
    ProgressButton submitMessage;
    @UiField
    MaterialTextArea message;
    @UiField
    UserWidget userImage;
    @UiField
    protected
    MaterialPanel tabs;
    @UiField
    MaterialButton status;
    @UiField
    MaterialDropDown statuses;
    @UiField
    MaterialLabel description;
    @UiField
    HTMLPanel responseTitle;
    @UiField
    MaterialRow requestTab;
    @UiField
    MaterialTab tab;
    @UiField
    MaterialPanel responsePanel;
    @UiField
    MaterialPanel colorPanel;
    @UiField
    MaterialLink requestLink;

    public RequestViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        userImage.setUser(Customer.getLoginInfo().getUserName());

        // add this to make sure the request tab is handled properly
        requestLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                selectTab("request");
            }
        });
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        mapContainer.setMapLoadedHandler(mapLoadedHandler);
    }

    @Override
    public void displayTitle(String title) {
        this.title.setText(title);
    }

    @Override
    public void displayComment(String comment) {
        description.setText(comment);
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
    public void displayResponseSupplier(String supplierIconUrl, String supplierName) {
        responseTitle.clear();
        responseTitle.add(new HTML("Offer provided by " +
                "<img style='max-height: 24px; vertical-align: middle;' src='" + supplierIconUrl + "'/> <b>" + supplierName + "</b></span>"));
    }

    @Override
    public void addMessage(String userName, boolean isCustomer, String message, Date date) {
        MaterialRow materialRow = new MaterialRow();
        materialRow.setMarginBottom(0);
        messages.add(materialRow);
        String colour = "white";
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
    }

    protected void displayAoI(AoIDTO aoi) {
        mapContainer.displayAoI(aoi);
        mapContainer.centerOnAoI();
    }

    protected void addRequestValue(String name, String value) {
        this.requestDescription.add(new HTMLPanel("<p style='padding: 0.5rem;'><b>" + name + ":</b> " +
                (value == null ? "not provided" : value) + "</p>"));
    }

    protected void displayResponse(String response) {
        requestResponse.clear();
        if(response == null) {
            MaterialLabel materialLabel = new MaterialLabel("This supplier hasn't provided an offer yet...");
            materialLabel.setMargin(20);
            materialLabel.setTextColor("grey");
            requestResponse.add(materialLabel);
        } else {
            requestResponse.add(new HTML(response));
        }
    }

    protected void displayMessages(List<MessageDTO> messages) {
        this.messages.clear();
        this.message.setText("");
        String userName = Customer.getLoginInfo().getUserName();
        if(messages.size() == 0) {
            MaterialLabel materialLabel = new MaterialLabel("No messages yet...");
            materialLabel.setMargin(20);
            materialLabel.setTextColor("grey");
            this.messages.add(materialLabel);
            message.setPlaceholder("Start a conversation...");
        } else {
            for (MessageDTO messageDTO : messages) {
                boolean isCustomer = !userName.contentEquals(messageDTO.getFrom());
                addMessage(messageDTO.getFrom(),
                        isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
            }
            message.setPlaceholder("Reply...");
        }
    }

    protected void setStatus(Request.STATUS status) {
        this.status.setText(status.toString());
        statuses.clear();
        this.status.setEnabled(false);
            switch(status) {
            case submitted:
                this.status.setEnabled(true);
            {
                MaterialLink materialLink = new MaterialLink("Cancel");
                materialLink.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        template.getClientFactory().getEventBus().fireEvent(new ChangeStatus(Request.STATUS.cancelled));
                    }
                });
                statuses.add(materialLink);
            }
            {
                MaterialLink materialLink = new MaterialLink("Complete");
                materialLink.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        template.getClientFactory().getEventBus().fireEvent(new ChangeStatus(Request.STATUS.completed));
                    }
                });
                statuses.add(materialLink);
            }
                break;
        }
    }

    protected void setCategory(Category category) {
        String color = CategoryUtils.getColor(category);
        tabs.setBackgroundColor(color);
        colorPanel.setBackgroundColor(color);
    }

    protected void resetTabs() {
        // remove all other tabs than the request one
        for(int index = tab.getWidgetCount(); index > 1; index--) {
            tab.remove(tab.getWidget(index));
        }
    }

    protected void addResponseTab(String id, String name, ClickHandler clickHandler) {
        MaterialTabItem materialTabItem = new MaterialTabItem();
        materialTabItem.setTextAlign(TextAlign.CENTER);
        MaterialColumn materialColumn = new MaterialColumn();
        MaterialLink materialLink = new MaterialLink(name);
        materialLink.setHref("#" + id);
        materialLink.setTextColor("white");
        materialTabItem.add(materialLink);
        materialLink.addClickHandler(clickHandler);
        materialColumn.add(materialLink);
        materialTabItem.add(materialColumn);
        tab.add(materialTabItem);
        MaterialPanel materialPanel = new MaterialPanel();
        materialPanel.setId(id);
        tabs.add(materialPanel);
    }

    protected void selectTab(String id) {
        tab.selectTab(id);
        responsePanel.setVisible(!id.contentEquals("request"));
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