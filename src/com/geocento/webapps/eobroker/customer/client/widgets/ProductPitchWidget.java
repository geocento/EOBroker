package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.shared.ProductDTO;
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
public class ProductPitchWidget extends Composite {

    interface ProductPitchUiBinder extends UiBinder<Widget, ProductPitchWidget> {
    }

    private static ProductPitchUiBinder ourUiBinder = GWT.create(ProductPitchUiBinder.class);

    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel pitchDescription;
    @UiField
    MaterialImageLoading imagePanel;

    public ProductPitchWidget(ProductDTO productDTO, String pitch) {

        initWidget(ourUiBinder.createAndBindUi(this));

        ((MaterialCard) getWidget()).setBackgroundColor(CategoryUtils.getColor(Category.software));

        FullViewPlace productPlace = new FullViewPlace(FullViewPlace.TOKENS.productid.toString() + "=" + productDTO.getId());

        imagePanel.setImageUrl(productDTO.getImageUrl());
        imagePanel.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(productPlace));

        title.setText(productDTO.getName());
        pitchDescription.setText(pitch);
    }

}