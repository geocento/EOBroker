package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.customer.shared.ProductServiceDTO;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.places.ProductFeasibilityPlace;
import com.geocento.webapps.eobroker.customer.client.places.ProductFormPlace;
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
    @UiField
    MaterialLink check;

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
        if(productServiceDTO.isHasFeasibility()) {
            check.setVisible(true);
            check.setHref("#" + PlaceHistoryHelper.convertPlace(
                    new ProductFeasibilityPlace(
/*
                            ProductFeasibilityPlace.TOKENS.product.toString() + "=" + productServiceDTO.getProduct().getId() + "&" +
*/
                            ProductFeasibilityPlace.TOKENS.productservice.toString() + "=" + productServiceDTO.getId())));
        }
        quote.setHref("#" + PlaceHistoryHelper.convertPlace(
                new ProductFormPlace(
                            ProductFormPlace.TOKENS.id.toString() + "=" + productServiceDTO.getProduct().getId() + "&" +
                            ProductFormPlace.TOKENS.serviceid.toString() + "=" + productServiceDTO.getId())));
    }

}