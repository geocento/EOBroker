package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.widgets.ProductTextBox;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.richeditor.MaterialRichEditor;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialTextArea;
import gwt.material.design.client.ui.MaterialTextBox;
import gwt.material.design.client.ui.MaterialTitle;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductServiceViewImpl extends Composite implements ProductServiceView {

    private Presenter presenter;

    interface ServicesViewUiBinder extends UiBinder<Widget, ProductServiceViewImpl> {
    }

    private static ServicesViewUiBinder ourUiBinder = GWT.create(ServicesViewUiBinder.class);
    @UiField
    MaterialTitle title;
    @UiField
    MaterialTextBox name;
    @UiField
    MaterialTextBox email;
    @UiField
    MaterialTextBox website;
    @UiField
    MaterialImageUploader imageUploader;
    @UiField
    MaterialTextArea description;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialRichEditor fullDescription;
    @UiField
    ProductTextBox product;
    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialTextBox sampleWmsUrl;
    @UiField
    MaterialTextBox apiURL;

    public ProductServiceViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void setTitleLine(String title) {
        this.title.setTitle(title);
    }

    @Override
    public HasText getName() {
        return name;
    }

    @Override
    public HasText getEmail() {
        return email;
    }

    @Override
    public HasText getWebsite() {
        return website;
    }

    @Override
    public HasText getDescription() {
        return description;
    }

    @Override
    public String getFullDescription() {
        return fullDescription.getHTML();
    }

    @Override
    public void setFullDescription(String fullDescription) {
        this.fullDescription.setHTML(fullDescription);
    }

    @Override
    public HasClickHandlers getSubmit() {
        return submit;
    }

    @Override
    public String getIconUrl() {
        return imageUploader.getImageUrl();
    }

    @Override
    public void setIconUrl(String iconURL) {
        imageUploader.setImageUrl(iconURL);
    }

    @Override
    public ProductDTO getSelectProduct() {
        return product.getProduct();
    }

    @Override
    public void setSelectedProduct(ProductDTO productDTO) {
        product.setProduct(productDTO);
    }

    @Override
    public HasText getAPIUrl() {
        return apiURL;
    }

    @Override
    public HasText getSampleWmsUrl() {
        return sampleWmsUrl;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

}