package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveProductDataset;
import com.geocento.webapps.eobroker.supplier.client.places.ProductDatasetPlace;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDatasetDTO;
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
public class ProductDatasetWidget extends Composite {

    interface DatasetProviderUiBinder extends UiBinder<Widget, ProductDatasetWidget> {
    }

    private static DatasetProviderUiBinder ourUiBinder = GWT.create(DatasetProviderUiBinder.class);

    @UiField
    MaterialCardAction action;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLink edit;
    @UiField
    MaterialLink remove;
    @UiField
    MaterialImageLoading imagePanel;

    public ProductDatasetWidget(final ProductDatasetDTO productDatasetDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));

        imagePanel.setImageUrl(productDatasetDTO.getImageUrl());
        imagePanel.addClickHandler(event -> Supplier.clientFactory.getPlaceController().goTo(new ProductDatasetPlace(ProductDatasetPlace.TOKENS.id.toString() + "=" + productDatasetDTO.getId())));

        title.setText(productDatasetDTO.getName());
        edit.addClickHandler(event -> Supplier.clientFactory.getPlaceController().goTo(new ProductDatasetPlace(ProductDatasetPlace.TOKENS.id.toString() + "=" + productDatasetDTO.getId())));
        remove.addClickHandler(event -> Supplier.clientFactory.getEventBus().fireEvent(new RemoveProductDataset(productDatasetDTO.getId())));
    }

}