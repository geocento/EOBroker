package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.shared.SoftwareDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 09/06/2016.
 */
public class SoftwareWidget extends Composite {

    interface SoftwareUiBinder extends UiBinder<Widget, SoftwareWidget> {
    }

    private static SoftwareUiBinder ourUiBinder = GWT.create(SoftwareUiBinder.class);

    @UiField
    MaterialImage image;
    @UiField
    MaterialLink companyLogo;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel shortDescription;
    @UiField
    MaterialLink information;

    public SoftwareWidget(SoftwareDTO softwareDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        Image logoImage = new Image(softwareDTO.getCompanyDTO().getIconURL());
        logoImage.setHeight("20px");
        companyLogo.add(logoImage);
        image.setUrl(softwareDTO.getImageUrl());
        title.setText(softwareDTO.getName());
        shortDescription.setText(softwareDTO.getDescription());
        information.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.softwareid.toString() + "=" + softwareDTO.getId())));
        companyLogo.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + softwareDTO.getCompanyDTO().getId())));
    }

}