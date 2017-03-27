package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveSoftware;
import com.geocento.webapps.eobroker.supplier.client.places.SoftwarePlace;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SoftwareDTO;
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
public class SoftwareWidget extends Composite {

    interface SoftwareUiBinder extends UiBinder<Widget, SoftwareWidget> {
    }

    private static SoftwareUiBinder ourUiBinder = GWT.create(SoftwareUiBinder.class);

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

    public SoftwareWidget(final SoftwareDTO softwareDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));

        imagePanel.setImageUrl(softwareDTO.getImageUrl());
        imagePanel.addClickHandler(event -> Supplier.clientFactory.getPlaceController().goTo(new SoftwarePlace(SoftwarePlace.TOKENS.id.toString() + "=" + softwareDTO.getId())));

        title.setText(softwareDTO.getName());
        edit.addClickHandler(event -> Supplier.clientFactory.getPlaceController().goTo(new SoftwarePlace(SoftwarePlace.TOKENS.id.toString() + "=" + softwareDTO.getId())));
        remove.addClickHandler(event -> Supplier.clientFactory.getEventBus().fireEvent(new RemoveSoftware(softwareDTO.getId())));
    }

}