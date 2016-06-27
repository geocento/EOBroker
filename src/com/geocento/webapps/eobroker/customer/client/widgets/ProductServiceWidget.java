package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.cutout.MaterialCutOut;
import gwt.material.design.client.ui.*;

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
    MaterialLink companyLogo;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialCardAction action;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel shortDescription;
    @UiField
    MaterialCutOut cutout;
    @UiField
    MaterialButton close;
    @UiField
    MaterialTitle cutoutTitle;
    @UiField
    MaterialLink quote;
    @UiField
    MaterialLink information;

    public ProductServiceWidget(ProductServiceDTO productServiceDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        Image logoImage = new Image(productServiceDTO.getCompanyLogo());
        logoImage.setHeight("20px");
        companyLogo.add(logoImage);
        image.setUrl(productServiceDTO.getServiceImage());
        title.setText(productServiceDTO.getName());
        shortDescription.setText(productServiceDTO.getDescription());
        description.setText(productServiceDTO.getDescription());
        information.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.productserviceid.toString() + "=" + productServiceDTO.getId())));
        companyLogo.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + productServiceDTO.getCompanyId())));
    }

}