package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialTextBox;

/**
 * Created by thomas on 08/11/2016.
 */
public class ProductPitchWidget extends Composite {

    interface ProductPitchWidgetUiBinder extends UiBinder<Widget, ProductPitchWidget> {
    }

    private static ProductPitchWidgetUiBinder ourUiBinder = GWT.create(ProductPitchWidgetUiBinder.class);

    @UiField
    ProductTextBox product;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialTextBox pitch;

    public ProductPitchWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setProduct(ProductDTO productDTO) {
        product.setProduct(productDTO);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setPitch(String pitch) {
        this.pitch.setText(pitch);
    }

    protected ProductDTO getProduct() {
        return product.getProduct();
    }

    protected String getPitch() {
        return pitch.getText();
    }

}