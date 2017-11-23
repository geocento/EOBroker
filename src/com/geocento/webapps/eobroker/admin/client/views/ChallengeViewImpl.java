package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.widgets.ProductWidget;
import com.geocento.webapps.eobroker.admin.shared.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ChallengeViewImpl extends Composite implements ChallengeView {

    private Presenter presenter;

    interface CompanyViewUiBinder extends UiBinder<Widget, ChallengeViewImpl> {
    }

    private static CompanyViewUiBinder ourUiBinder = GWT.create(CompanyViewUiBinder.class);
    @UiField
    MaterialTextBox name;
    @UiField
    MaterialTextArea description;
    @UiField
    com.geocento.webapps.eobroker.common.client.widgets.material.MaterialRichEditor fullDescription;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialImageUploader imageUploader;
    @UiField
    MaterialTitle title;
    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialRow products;

    public ChallengeViewImpl(ClientFactoryImpl clientFactory) {

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
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void setLoading(String message) {
        template.setLoading(message);
    }

    @Override
    public void setLoadingError(String message) {
        template.setLoading(message);
    }

    @Override
    public void hideLoading(String message) {
        template.hideLoading(message);
    }

    @Override
    public void setProducts(List<ProductDTO> productDTOs) {
        products.clear();
        if(ListUtil.isNullOrEmpty(productDTOs)) {
            products.add(new MaterialLabel("No products"));
        } else {
            for(ProductDTO productDTO : productDTOs) {
                addProduct(productDTO);
            }
        }
    }

    private void addProduct(ProductDTO productDTO) {
        MaterialColumn materialColumn = new MaterialColumn(12, 4, 3);
        products.add(materialColumn);
        // TODO - change for another product widget
        materialColumn.add(new ProductWidget(productDTO));
    }

}