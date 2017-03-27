package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.shared.ProductDatasetDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCard;
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
    MaterialLink info;
    @UiField
    MaterialLink companyLogo;
    @UiField
    MaterialLabel shortDescription;
    @UiField
    MaterialLink stars;
    @UiField
    MaterialImageLoading imagePanel;

    public ProductDatasetWidget(final ProductDatasetDTO productDatasetDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));

        ((MaterialCard) getWidget()).setBackgroundColor(CategoryUtils.getColor(Category.productdatasets));

        imagePanel.setImageUrl(productDatasetDTO.getImageUrl());
        imagePanel.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.productdatasetid.toString() + "=" + productDatasetDTO.getId())));

        title.setText(productDatasetDTO.getName());
        shortDescription.setText(productDatasetDTO.getDescription());
        Image logoImage = new Image(productDatasetDTO.getCompany().getIconURL());
        logoImage.setHeight("20px");
        companyLogo.add(logoImage);
        companyLogo.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + productDatasetDTO.getCompany().getId())));
        info.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.productdatasetid.toString() + "=" + productDatasetDTO.getId())));
    }

}