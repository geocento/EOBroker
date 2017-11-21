package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.utils.DateUtil;
import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.*;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.entities.requests.Request;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.ChangeStatus;
import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceSupplierResponseDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import gwt.material.design.addins.client.bubble.MaterialBubble;
import gwt.material.design.addins.client.rating.MaterialRating;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.Position;
import gwt.material.design.client.ui.*;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductResponseViewImpl extends Composite implements ProductResponseView {

    interface ProductResponseViewUiBinder extends UiBinder<Widget, ProductResponseViewImpl> {
    }

    private static ProductResponseViewUiBinder ourUiBinder = GWT.create(ProductResponseViewUiBinder.class);

    static public interface StyleFile extends CssResource {
        String sectionLabel();
    }

    @UiField
    StyleFile style;

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
    MaterialPanel responsePanel;
    @UiField
    MaterialPanel colorPanel;
    @UiField
    MaterialPanel responsesPanel;
    @UiField
    MaterialPanel offers;
    @UiField
    MaterialImageLoading image;
    @UiField
    MaterialLabel messagesComment;
    @UiField
    MaterialLabel creationTime;

    private ClientFactoryImpl clientFactory;

    private ProductResponseView.Presenter presenter;

    public ProductResponseViewImpl(ClientFactoryImpl clientFactory) {

        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

        colorPanel.setBackgroundColor(CategoryUtils.getColor(Category.productservices));

        userImage.setUser(Customer.getLoginInfo().getUserName());

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
    public void displayImage(String imageURL) {
        this.image.setImageUrl(imageURL);
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
    public void displayResponseSupplier(String supplierIconUrl, String supplierName) {
        responseTitle.clear();
        responseTitle.add(new HTML("Offer provided by " +
                //"<img style='max-height: 24px; vertical-align: middle;' src='" + supplierIconUrl + "'/> " +
                "<b>" + supplierName + "</b>"));
    }

    @Override
    public void addMessage(String userName, boolean isCustomer, String message, Date date) {
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
        updateMessagesComment();
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
            materialLabel.setTextColor(Color.GREY);
            requestResponse.add(materialLabel);
        } else {
            requestResponse.add(new HTML(response));
        }
    }

    protected void displayMessages(List<MessageDTO> messages) {
        this.messages.clear();
        this.message.setText("");
        String userName = Customer.getLoginInfo().getUserName();
        if(messages != null && messages.size() > 0) {
            for (MessageDTO messageDTO : messages) {
                boolean isCustomer = !userName.contentEquals(messageDTO.getFrom());
                addMessage(messageDTO.getFrom(),
                        isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
            }
        }
        updateMessagesComment();
    }

    private void updateMessagesComment() {
        boolean hasMessages = messages.getWidgetCount() > 0;
        messagesComment.setVisible(!hasMessages);
        if(!hasMessages) {
            messagesComment.setText("No messages yet...");
            message.setPlaceholder("Start a conversation...");
        } else {
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
                        clientFactory.getEventBus().fireEvent(new ChangeStatus(Request.STATUS.cancelled));
                    }
                });
                statuses.add(materialLink);
            }
            {
                MaterialLink materialLink = new MaterialLink("Complete");
                materialLink.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        clientFactory.getEventBus().fireEvent(new ChangeStatus(Request.STATUS.completed));
                    }
                });
                statuses.add(materialLink);
            }
            break;
        }
    }

    protected void addResponseSection(ProductServiceSupplierResponseDTO selectedOffer) {
        ExpandPanel expandPanel = new ExpandPanel();
        MaterialRating materialRating = new MaterialRating();
        materialRating.setFloat(Style.Float.RIGHT);
        expandPanel.setHeader(materialRating);
        expandPanel.setLabel("Offer from '" + selectedOffer.getCompany().getName() + "' for service '" + selectedOffer.getServiceName() + "'");
        expandPanel.setOpenHandler(new ExpandPanel.OpenHandler() {
            @Override
            public void onOpen() {
                presenter.responseSelected(selectedOffer);
            }
        });
        expandPanel.setContent(new LoadingWidget("Loading..."));
        expandPanel.setId(selectedOffer.getId() + "");
        expandPanel.setOpen(false);
        expandPanel.setLabelStyle(style.sectionLabel());
        expandPanel.setLabelColor(Color.WHITE);
        offers.add(expandPanel);
    }

    @Override
    public HasClickHandlers getSubmitMessage() {
        return submitMessage;
    }

    @Override
    public HasText getMessageText() {
        return message;
    }

    @Override
    public void setPresenter(ProductResponseView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayProductRequest(ProductServiceResponseDTO productServiceResponseDTO) {
        setStatus(productServiceResponseDTO.getStatus());
        creationTime.setText("Created on " + DateUtil.displaySimpleUTCDate(productServiceResponseDTO.getCreationTime()));
        this.requestDescription.clear();
        addRequestValue("Product requested", productServiceResponseDTO.getProduct().getName());
        if(productServiceResponseDTO.getFormValues().size() == 0) {
            this.requestDescription.add(new HTMLPanel("<p>No data provided</p>"));
        } else {
            for (FormElementValue formElementValue : productServiceResponseDTO.getFormValues()) {
                addRequestValue(formElementValue.getName(), formElementValue.getValue());
            }
        }
        displayAoI(AoIUtil.fromWKT(productServiceResponseDTO.getAoIWKT()));
        // now add the responses
        offers.clear();
        List<ProductServiceSupplierResponseDTO> responses = productServiceResponseDTO.getSupplierResponses();
        for(final ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO : responses) {
            addResponseSection(productServiceSupplierResponseDTO);
        }
/*
        if(responses.size() > 1) {
            for(final ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO : responses) {
                addResponseSection(productServiceSupplierResponseDTO);
            }
            this.responses.setVisible(true);
            // move response panel out of the collection widget
            responsesPanel.add(responsePanel);
        } else {
            this.responses.setVisible(false);
            // move response panel out of the collection widget
            responsesPanel.add(responsePanel);
        }
*/
        displayProductResponse(responses.get(0));
    }

    @Override
    public void displayProductResponse(ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO) {
        if(productServiceSupplierResponseDTO == null) {
            return;
        }
        for(Widget widget : offers.getChildren()) {
            if(widget instanceof ExpandPanel) {
                ((ExpandPanel) widget).setOpen(false);
                if(((ExpandPanel) widget).getId().contentEquals(productServiceSupplierResponseDTO.getId() + "")) {
                    ((ExpandPanel) widget).setContent(responsePanel);
                    displayResponseSupplier(productServiceSupplierResponseDTO.getCompany().getIconURL(), productServiceSupplierResponseDTO.getCompany().getName());
                    displayResponse(productServiceSupplierResponseDTO.getResponse());
                    displayMessages(productServiceSupplierResponseDTO.getMessages());
                    ((ExpandPanel) widget).setOpen(true);
                }
            }
        }
    }

}