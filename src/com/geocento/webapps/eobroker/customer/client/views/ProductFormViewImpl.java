package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.forms.ElementEditor;
import com.geocento.webapps.eobroker.common.client.widgets.forms.TextEditor;
import com.geocento.webapps.eobroker.common.shared.entities.AoIFormElement;
import com.geocento.webapps.eobroker.common.shared.entities.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.TextFormElement;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCheckBox;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductFormViewImpl extends Composite implements ProductFormView {

    private Presenter presenter;

    interface ProductFormUiBinder extends UiBinder<Widget, ProductFormViewImpl> {
    }

    private static ProductFormUiBinder ourUiBinder = GWT.create(ProductFormUiBinder.class);

    @UiField(provided = true)
    TemplateView template;
    @UiField
    HTMLPanel formContainer;
    @UiField
    HTMLPanel suppliers;
    @UiField
    MaterialImage image;
    @UiField
    MaterialLabel name;
    @UiField
    MaterialLink information;
    @UiField
    MaterialLabel description;

    public ProductFormViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        template.setTitleText("Product form");
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clearForm() {
        formContainer.clear();
    }

    @Override
    public void addFormElement(FormElement formElement) {
        ElementEditor editor = createEditor(formElement);
        formContainer.add(editor);
    }

    private ElementEditor createEditor(final FormElement formElement) {
        if(formElement instanceof AoIFormElement) {

        } else if(formElement instanceof TextFormElement) {
            TextEditor textEditor = new TextEditor();
            textEditor.setFormElement((TextFormElement) formElement);
            return textEditor;
        }
        return null;
    }

    @Override
    public void clearSuppliers() {
        suppliers.clear();
    }

    @Override
    public void addSupplier(ProductServiceDTO productServiceDTO) {
        MaterialCheckBox materialCheckBox =
                new MaterialCheckBox("<span style='display: inline;'><b>" + productServiceDTO.getName() + "</b> " +
                        "by <img style='max-height: 24px; vertical-align: middle;' src='" + productServiceDTO.getCompanyLogo() + "'/> <b>" + productServiceDTO.getCompanyName() + "</b></span>", true);
        materialCheckBox.setObject(productServiceDTO);
        suppliers.add(materialCheckBox);
    }

    @Override
    public void setProductImage(String imageUrl) {
        image.setUrl(imageUrl);
    }

    @Override
    public void setProductName(String name) {
        this.name.setText(name);
    }

    @Override
    public void setProductDescription(String description) {
        this.description.setText(description);
    }

    @Override
    public void displayLoading(String message) {
        template.setLoading(message);
    }

    @Override
    public void hideLoading() {
        template.hideLoading();
    }

    @Override
    public void displayError(String message) {
        template.displayError(message);
    }

    @Override
    public void displaySuccess(String message) {
        template.displaySuccess(message);
    }

    @Override
    public void displayLoading() {
        template.displayLoading();
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}