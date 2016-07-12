package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.richeditor.MaterialRichEditor;
import gwt.material.design.client.base.SearchObject;
import gwt.material.design.client.events.SearchFinishEvent;
import gwt.material.design.client.ui.*;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ServicesViewImpl extends Composite implements ServicesView {

    private Presenter presenter;

    interface ServicesViewUiBinder extends UiBinder<Widget, ServicesViewImpl> {
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
    MaterialSearch product;
    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialTextBox sampleWmsUrl;
    @UiField
    MaterialTextBox apiURL;

    public ServicesViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        product.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                REST.withCallback(new MethodCallback<List<ProductDTO>>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, List<ProductDTO> response) {
                        product.setListSearches(ListUtil.mutate(response, new ListUtil.Mutate<ProductDTO, SearchObject>() {
                            @Override
                            public SearchObject mutate(ProductDTO productDTO) {
                                return new SearchObject(productDTO.getName(), "", productDTO);
                            }
                        }));
                    }
                }).call(ServicesUtil.assetsService).findProducts(product.getValue());
            }
        });
        product.addSearchFinishHandler(new SearchFinishEvent.SearchFinishHandler() {
            @Override
            public void onSearchFinish(SearchFinishEvent event) {

            }
        });
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
        return (ProductDTO) product.getSelectedObject().getO();
    }

    @Override
    public void setSelectedProduct(ProductDTO productDTO) {
        product.setSelectedObject(productDTO == null ? null : new SearchObject(productDTO.getName(), "", productDTO));
        product.setText(productDTO.getName());
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
    public TemplateView getTemplateView() {
        return template;
    }

}