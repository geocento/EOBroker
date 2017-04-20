package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.shared.SoftwareDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCard;
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
    MaterialLink companyLogo;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel shortDescription;
    @UiField
    MaterialImageLoading imagePanel;
    @UiField
    MaterialLink info;

    public SoftwareWidget(SoftwareDTO softwareDTO) {

        initWidget(ourUiBinder.createAndBindUi(this));

        ((MaterialCard) getWidget()).setBackgroundColor(CategoryUtils.getColor(Category.software));

        imagePanel.setImageUrl(softwareDTO.getImageUrl());
        imagePanel.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.softwareid.toString() + "=" + softwareDTO.getId())));

        Image logoImage = new Image(softwareDTO.getCompanyDTO().getIconURL());
        companyLogo.add(logoImage);

        title.setText(softwareDTO.getName());
        shortDescription.setText(softwareDTO.getDescription());
        companyLogo.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + softwareDTO.getCompanyDTO().getId())));
        info.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.softwareid.toString() + "=" + softwareDTO.getId())));
    }

}