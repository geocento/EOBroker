package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.shared.ProductDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
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
    MaterialLink information;
    @UiField
    MaterialImage imageLoading;

    public ProductWidget(final ProductDTO productDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                image.setVisible(true);
                imageLoading.setVisible(false);
            }
        });
        image.setUrl(productDTO.getImageUrl() == null || productDTO.getImageUrl().length() == 0 ? "./images/noImage.png" : productDTO.getImageUrl());
        title.setText(productDTO.getName());
        description.setText(productDTO.getDescription());
        information.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.productid.toString() + "=" + productDTO.getId())));
    }
}