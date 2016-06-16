package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.RequestImagery;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialLabel;

/**
 * Created by thomas on 09/06/2016.
 */
public class ImageRequestWidget extends Composite {

    interface ImageRequestWidgetUiBinder extends UiBinder<Widget, ImageRequestWidget> {
    }

    private static ImageRequestWidgetUiBinder ourUiBinder = GWT.create(ImageRequestWidgetUiBinder.class);

    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialButton request;

    public ImageRequestWidget(String text) {
        initWidget(ourUiBinder.createAndBindUi(this));
        title.setText("Request Imagery");
        description.setText("Contact our large range of imagery suppliers and request a quotation for imagery matchin '" + text + "'.");
        request.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Customer.clientFactory.getEventBus().fireEvent(new RequestImagery());
            }
        });
    }
}