package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveService;
import com.geocento.webapps.eobroker.supplier.client.places.ProductServicePlace;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCardAction;
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
    MaterialCardAction action;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel shortDescription;
    @UiField
    MaterialLink edit;
    @UiField
    MaterialLink remove;
    @UiField
    MaterialImageLoading imagePanel;

    private ProductServiceDTO productService;

    public ProductServiceWidget(final ProductServiceDTO productServiceDTO) {

        this.productService = productServiceDTO;

        initWidget(ourUiBinder.createAndBindUi(this));

        imagePanel.setImageUrl(productServiceDTO.getServiceImage());
        imagePanel.addClickHandler(event -> Supplier.clientFactory.getPlaceController().goTo(new ProductServicePlace(ProductServicePlace.TOKENS.service.toString() + "=" + productServiceDTO.getId())));

        title.setText(productServiceDTO.getName());
        shortDescription.setText(productServiceDTO.getDescription());
        edit.addClickHandler(event -> Supplier.clientFactory.getPlaceController().goTo(new ProductServicePlace(ProductServicePlace.TOKENS.service.toString() + "=" + productServiceDTO.getId())));
        remove.addClickHandler(event -> Supplier.clientFactory.getEventBus().fireEvent(new RemoveService(productServiceDTO.getId())));
    }

    public ProductServiceDTO getProductService() {
        return productService;
    }

}