package com.geocento.webapps.eobroker.client.widgets;

import com.geocento.webapps.eobroker.shared.entities.dtos.ProductServiceDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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

    public ProductServiceWidget(ProductServiceDTO productServiceDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        Image logoImage = new Image(productServiceDTO.getCompanyLogo());
        logoImage.setHeight("20px");
        companyLogo.add(logoImage);
        image.setUrl(productServiceDTO.getServiceImage());
        title.setText(productServiceDTO.getName());
        shortDescription.setText(productServiceDTO.getDescription());
        description.setText(productServiceDTO.getDescription());
    }

    @UiHandler("companyLogo")
    void companyDescription(ClickEvent clickEvent) {
        cutout.setTarget(companyLogo);
        cutoutTitle.setTitle("Description of company");
        cutoutTitle.setDescription("This will eventually move to a company page");
        cutout.openCutOut();
    }

    @UiHandler("quote")
    void quote(ClickEvent clickEvent) {
        cutout.setTarget(companyLogo);
        cutoutTitle.setTitle("Request Quote");
        cutoutTitle.setDescription("This will eventually move to a request quote page where you can enter your parameters and submit a request for quotation");
        cutout.openCutOut();
    }

    @UiHandler("information")
    void information(ClickEvent clickEvent) {
        cutout.setTarget(companyLogo);
        cutoutTitle.setTitle("View Information");
        cutoutTitle.setDescription("This will eventually move to a view information page where you can get more information and view sample products");
        cutout.openCutOut();
    }

    @UiHandler("close")
    void close(ClickEvent clickEvent) {
        cutout.closeCutOut();
    }
}