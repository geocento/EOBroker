package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by thomas on 27/03/2017.
 */
public class MaterialImageLoading extends Composite implements HasClickHandlers {

    interface MaterialImageLoadingUiBinder extends UiBinder<MaterialPanel, MaterialImageLoading> {
    }

    private static MaterialImageLoadingUiBinder ourUiBinder = GWT.create(MaterialImageLoadingUiBinder.class);

    @UiField
    MaterialPanel panel;
    @UiField
    MaterialImage imageLoading;
    @UiField
    MaterialImage image;

    public MaterialImageLoading() {

        initWidget(ourUiBinder.createAndBindUi(this));

        image.addLoadHandler(event -> {
            image.setVisible(true);
            imageLoading.setVisible(false);
        });
    }

    public void setImageUrl(String imageUrl) {
        image.setUrl(imageUrl == null || imageUrl.length() == 0 ? "./images/noImage.png" : imageUrl);
    }

    public void setLoadingResource(ImageResource imageResource) {
        imageLoading.setResource(imageResource);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return panel.addClickHandler(handler);
    }

}