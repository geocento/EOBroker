package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveService;
import com.geocento.webapps.eobroker.supplier.client.places.ProductServicePlace;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCardAction;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 09/06/2016.
 */
public class ProductServiceWidget extends Composite {

    interface ProductServiceUiBinder extends UiBinder<Widget, ProductServiceWidget> {
    }

    private static ProductServiceUiBinder ourUiBinder = GWT.create(ProductServiceUiBinder.class);

    @UiField
    MaterialImage image;
    @UiField
    MaterialLink companyLogo;
    @UiField
    MaterialCardAction action;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel shortDescription;
    @UiField
    MaterialLink edit;
    @UiField
    MaterialLink remove;

    public ProductServiceWidget(final ProductServiceDTO productServiceDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        Image logoImage = new Image(productServiceDTO.getCompanyLogo());
        logoImage.setHeight("20px");
        companyLogo.add(logoImage);
        image.setUrl(productServiceDTO.getServiceImage());
        title.setText(productServiceDTO.getName());
        shortDescription.setText(productServiceDTO.getDescription());
        edit.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Supplier.clientFactory.getPlaceController().goTo(new ProductServicePlace(ProductServicePlace.TOKENS.service.toString() + "=" + productServiceDTO.getId()));
            }
        });
        remove.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Supplier.clientFactory.getEventBus().fireEvent(new RemoveService(productServiceDTO.getId()));
            }
        });
    }

}