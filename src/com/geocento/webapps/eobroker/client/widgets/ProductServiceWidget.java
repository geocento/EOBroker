package com.geocento.webapps.eobroker.client.widgets;

import com.geocento.webapps.eobroker.shared.entities.dtos.ProductServiceDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCardAction;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;

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
    MaterialImage companyLogo;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialCardAction action;

    public ProductServiceWidget(ProductServiceDTO productServiceDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        companyLogo.setUrl(productServiceDTO.getCompanyLogo());
        image.setUrl(productServiceDTO.getServiceImage());
        description.setText(productServiceDTO.getDescription());
    }
}