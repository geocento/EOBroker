package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 09/06/2016.
 */
public class ProductPitchWidget extends Composite {

    interface CompanyWidgetUiBinder extends UiBinder<Widget, ProductPitchWidget> {
    }

    private static CompanyWidgetUiBinder ourUiBinder = GWT.create(CompanyWidgetUiBinder.class);

    @UiField
    MaterialImage image;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel pitch;
    @UiField
    MaterialImage imageLoading;
    @UiField
    MaterialLink remove;

    private ProductProjectPitch productProjectPitch;

    public ProductPitchWidget() {

        initWidget(ourUiBinder.createAndBindUi(this));

        ((MaterialCard) getWidget()).setBackgroundColor(CategoryUtils.getColor(Category.products));

        image.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                image.setVisible(true);
                imageLoading.setVisible(false);
            }
        });
    }

    protected void setProduct(ProductDTO productDTO) {
        image.setUrl(productDTO.getImageUrl() == null ? "./images/noImage.png" : productDTO.getImageUrl());
        title.setText(productDTO.getName());
    }

    protected void setPitch(String pitch) {
        this.pitch.setText(pitch);
    }

    protected String getPitch() {
        return pitch.getText();
    }

    public HasClickHandlers getRemove() {
        return remove;
    }

}