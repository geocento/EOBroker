package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.shared.ProductDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductSoftwareDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialLabel;

/**
 * Created by thomas on 09/06/2016.
 */
public class ProductSoftwareWidget extends Composite {

    interface ProductSoftwareUiBinder extends UiBinder<Widget, ProductSoftwareWidget> {
    }

    private static ProductSoftwareUiBinder ourUiBinder = GWT.create(ProductSoftwareUiBinder.class);

    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel pitchDescription;
    @UiField
    MaterialImageLoading imagePanel;

    public ProductSoftwareWidget(ProductSoftwareDTO softwareDTO) {

        initWidget(ourUiBinder.createAndBindUi(this));

        ((MaterialCard) getWidget()).setBackgroundColor(CategoryUtils.getColor(Category.software));

        ProductDTO product = softwareDTO.getProduct();

        FullViewPlace productPlace = new FullViewPlace(FullViewPlace.TOKENS.productid.toString() + "=" + product.getId());

        imagePanel.setImageUrl(product.getImageUrl());
        imagePanel.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(productPlace));

        title.setText(product.getName());
        pitchDescription.setText(softwareDTO.getPitch());
    }

}