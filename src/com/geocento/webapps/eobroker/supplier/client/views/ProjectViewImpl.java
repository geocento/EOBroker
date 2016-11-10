package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.widgets.ProductProjectPitch;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductProjectDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.richeditor.MaterialRichEditor;
import gwt.material.design.client.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProjectViewImpl extends Composite implements ProjectView {

    private Presenter presenter;

    interface ProjectViewUiBinder extends UiBinder<Widget, ProjectViewImpl> {
    }

    private static ProjectViewUiBinder ourUiBinder = GWT.create(ProjectViewUiBinder.class);
    @UiField
    MaterialTextBox name;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialImageUploader imageUploader;
    @UiField
    MaterialTitle title;
    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialTextArea description;
    @UiField
    MaterialRichEditor fullDescription;
    @UiField
    MaterialRow products;
    @UiField
    MaterialButton addProduct;

    public ProjectViewImpl(ClientFactoryImpl clientFactory) {

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
    public HasClickHandlers getSubmit() {
        return submit;
    }

    @Override
    public String getImageUrl() {
        return imageUploader.getImageUrl();
    }

    @Override
    public void setIconUrl(String iconURL) {
        imageUploader.setImageUrl(iconURL);
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
    public List<ProductProjectDTO> getSelectedProducts() {
        ArrayList<ProductProjectDTO> productProjectDTOs = new ArrayList<ProductProjectDTO>();
        for(int index = 0; index < products.getWidgetCount(); index++) {
            Widget widget = products.getWidget(index);
            if(widget instanceof ProductProjectPitch) {
                productProjectDTOs.add(((ProductProjectPitch) widget).getProductProjectDTO());
            }
        }
        return productProjectDTOs;
    }

    @Override
    public void setSelectedProducts(List<ProductProjectDTO> selectedProducts) {
        products.clear();
        if(selectedProducts == null || selectedProducts.size() == 0) {
            products.add(new MaterialLabel("No products added yet, use the add product button to add a product"));
        } else {
            for(ProductProjectDTO productProjectDTO : selectedProducts) {
                addProductProjectPitch(productProjectDTO);
            }
        }
    }

    private void addProductProjectPitch(ProductProjectDTO productProjectDTO) {
        // make sure we remove the text before
        if(getSelectedProducts().size() == 0) {
            products.clear();
        }
        ProductProjectPitch productProjectPitch = new ProductProjectPitch();
        productProjectPitch.setTitle("Product supported #" + (getSelectedProducts().size() + 1));
        productProjectPitch.setProductProject(productProjectDTO);
        products.add(productProjectPitch);
    }

    @UiHandler("addProduct")
    void addProduct(ClickEvent clickEvent) {
        addProductProjectPitch(new ProductProjectDTO());
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

}