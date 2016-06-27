package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.places.ProductFormPlace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

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
    @UiField
    MaterialLink quote;
    @UiField
    MaterialLink information;

    public ProductWidget(ProductDTO productDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        image.setUrl(productDTO.getImageUrl());
        title.setText(productDTO.getName());
        description.setText(productDTO.getDescription());
        quote.setHref("#" + PlaceHistoryHelper.convertPlace(new ProductFormPlace(productDTO.getId())));
        information.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.productid.toString() + "=" + productDTO.getId())));
    }
}