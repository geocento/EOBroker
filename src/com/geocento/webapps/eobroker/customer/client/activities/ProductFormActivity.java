package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.ProductFormPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.ProductFormView;
import com.geocento.webapps.eobroker.customer.shared.ProductFormDTO;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductFormActivity extends AbstractApplicationActivity implements ProductFormView.Presenter {

    private ProductFormView productFormView;

    public ProductFormActivity(ProductFormPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        productFormView = clientFactory.getProductFormView();
        productFormView.setPresenter(this);
        panel.setWidget(productFormView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    @Override
    protected void bind() {

    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        Long productId = null;
        if(tokens.containsKey(ProductFormPlace.TOKENS.id.toString())) {
            try {
                productId = Long.parseLong(tokens.get(ProductFormPlace.TOKENS.id.toString()));
            } catch (Exception e) {

            }
        }

        productFormView.displayLoading();
        try {
            REST.withCallback(new MethodCallback<ProductFormDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    productFormView.hideLoading();
                    productFormView.displayError("Could get product");
                }

                @Override
                public void onSuccess(Method method, ProductFormDTO productFormDTO) {
                    productFormView.hideLoading();
                    productFormView.setProductImage(productFormDTO.getImageUrl());
                    productFormView.setProductName(productFormDTO.getName());
                    productFormView.setProductDescription(productFormDTO.getDescription());
                    productFormView.clearForm();
                    for (FormElement formElement : productFormDTO.getFormFields()) {
                        productFormView.addFormElement(formElement);
                    }
                    productFormView.clearSuppliers();
                    for(ProductServiceDTO productServiceDTO : productFormDTO.getProductServices()) {
                        productFormView.addSupplier(productServiceDTO);
                    }
                }
            }).call(ServicesUtil.assetsService).getProductForm(productId);
        } catch (RequestException e) {
        }

    }

}
