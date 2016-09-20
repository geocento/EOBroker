package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.ImageryResponsePlace;
import com.geocento.webapps.eobroker.customer.client.places.ImagesResponsePlace;
import com.geocento.webapps.eobroker.customer.client.places.ProductResponsePlace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialCardContent;
import gwt.material.design.client.ui.MaterialLabel;

/**
 * Created by thomas on 09/06/2016.
 */
public class RequestWidget extends Composite {

    interface RequestWidgetUiBinder extends UiBinder<Widget, RequestWidget> {
    }

    private static RequestWidgetUiBinder ourUiBinder = GWT.create(RequestWidgetUiBinder.class);

    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialButton request;
    @UiField
    MaterialCardContent content;

    public RequestWidget(final RequestDTO requestDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        Place place = null;
        switch (requestDTO.getType()) {
            case image:
                title.setText("Request for images");
                content.setBackgroundColor("grey");
                place = new ImagesResponsePlace(ImageryResponsePlace.TOKENS.id.toString() + "=" + requestDTO.getId());
                break;
            case imageservice:
                title.setText("Request for image service");
                content.setBackgroundColor("orange");
                place = new ImageryResponsePlace(ImagesResponsePlace.TOKENS.id.toString() + "=" + requestDTO.getId());
                break;
            case imageprocessing:
                title.setText("Request for image processing");
                content.setBackgroundColor("green");
                break;
            case product:
                title.setText("Request for product");
                content.setBackgroundColor("blue-grey");
                place = new ProductResponsePlace(ProductResponsePlace.TOKENS.id.toString() + "=" + requestDTO.getId());
                break;
        }
        description.setText(requestDTO.getDescription());
        final Place finalPlace = place;
        request.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Customer.clientFactory.getPlaceController().goTo(finalPlace);
            }
        });
    }
}