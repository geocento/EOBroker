package com.geocento.webapps.eobroker.client.widgets;

import com.geocento.webapps.eobroker.shared.entities.dtos.ProductDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCardTitle;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;

/**
 * Created by thomas on 09/06/2016.
 */
public class ProductWidget extends Composite {

    interface ProductWidgetUiBinder extends UiBinder<Widget, ProductWidget> {
    }

    private static ProductWidgetUiBinder ourUiBinder = GWT.create(ProductWidgetUiBinder.class);

    @UiField
    MaterialImage image;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;

    public ProductWidget(ProductDTO productDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        image.setUrl(productDTO.getImageUrl());
        title.setText(productDTO.getName());
        description.setText(productDTO.getDescription());
    }
}