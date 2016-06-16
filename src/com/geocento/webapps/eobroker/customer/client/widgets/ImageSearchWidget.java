package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.SearchImagery;
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
public class ImageSearchWidget extends Composite {

    interface ImageSearchWidgetUiBinder extends UiBinder<Widget, ImageSearchWidget> {
    }

    private static ImageSearchWidgetUiBinder ourUiBinder = GWT.create(ImageSearchWidgetUiBinder.class);

    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialButton search;

    public ImageSearchWidget(String text) {
        initWidget(ourUiBinder.createAndBindUi(this));
        title.setText("Search Images");
        description.setText("Search for imagery relevant to '" + text + "'. " +
                "Find all available and potential imagery from a wide range of suppliers.");
        search.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Customer.clientFactory.getEventBus().fireEvent(new SearchImagery());
            }
        });
    }
}