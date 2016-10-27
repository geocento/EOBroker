package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.admin.client.events.RemoveProduct;
import com.geocento.webapps.eobroker.admin.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.admin.client.places.ProductPlace;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
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
    MaterialLink edit;
    @UiField
    MaterialLink remove;

    public ProductWidget(final ProductDTO productDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        image.setUrl(productDTO.getImageUrl() == null || productDTO.getImageUrl().length() == 0 ? "./images/noImage.png" : productDTO.getImageUrl());
        title.setText(productDTO.getName());
        description.setText(productDTO.getDescription());
        edit.setHref("#" + PlaceHistoryHelper.convertPlace(new ProductPlace(productDTO.getId())));
        remove.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(Window.confirm("Are you sure you want to remove this product?")) {
                    Admin.clientFactory.getEventBus().fireEvent(new RemoveProduct(productDTO.getId()));
                }
            }
        });
    }
}