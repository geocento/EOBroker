package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.widgets.ProductSoftwarePitch;
import com.geocento.webapps.eobroker.supplier.client.widgets.ProductTextBox;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductSoftwareDTO;
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
public class SoftwareViewImpl extends Composite implements SoftwareView {

    private Presenter presenter;

    interface SoftwareViewUiBinder extends UiBinder<Widget, SoftwareViewImpl> {
    }

    private static SoftwareViewUiBinder ourUiBinder = GWT.create(SoftwareViewUiBinder.class);

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
    @UiField
    ProductTextBox product;
    @UiField
    MaterialTextBox productPitch;
    @UiField
    MaterialLink viewClient;

    public SoftwareViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        template.setPlace("software");
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
    public List<ProductSoftwareDTO> getSelectedProducts() {
        ArrayList<ProductSoftwareDTO> productSoftwareDTOs = new ArrayList<ProductSoftwareDTO>();
        for(int index = 0; index < products.getWidgetCount(); index++) {
            Widget widget = products.getWidget(index);
            if(widget instanceof ProductSoftwarePitch) {
                productSoftwareDTOs.add(((ProductSoftwarePitch) widget).getProductSoftwareDTO());
            }
        }
        return productSoftwareDTOs;
    }

    @Override
    public void setSelectedProducts(List<ProductSoftwareDTO> selectedProducts) {
        products.clear();
        if(selectedProducts == null || selectedProducts.size() == 0) {
            products.add(new MaterialLabel("No products added yet, use the add product button to add a product"));
        } else {
            for (ProductSoftwareDTO productSoftwareDTO : selectedProducts) {
                addProductSoftwarePicth(productSoftwareDTO);
            }
        }
    }

    private void addProductSoftwarePicth(ProductSoftwareDTO productSoftwareDTO) {
        // make sure we remove the text before
        if(getSelectedProducts().size() == 0) {
            products.clear();
        }
        ProductSoftwarePitch productSoftwarePitch = new ProductSoftwarePitch();
        productSoftwarePitch.setTitle("Product supported #" + (getSelectedProducts().size() + 1));
        productSoftwarePitch.setProductSoftware(productSoftwareDTO);
        products.add(productSoftwarePitch);
    }

    @UiHandler("addProduct")
    void addProduct(ClickEvent clickEvent) {
        ProductDTO productDTO = product.getProduct();
        if(productDTO == null) {
            template.displayError("Please select a product");
            return;
        }
        String pitch = productPitch.getText();
        if(pitch.contentEquals("")) {
            template.displayError("Please provide a pitch for this product");
            return;
        }
        ProductSoftwareDTO productSoftwareDTO = new ProductSoftwareDTO();
        productSoftwareDTO.setProduct(productDTO);
        productSoftwareDTO.setPitch(pitch);
        addProductSoftwarePicth(productSoftwareDTO);
        // reset the values
        product.clearProduct();
        productPitch.setText("");
    }

    @Override
    public HasClickHandlers getViewClient() {
        return viewClient;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

}