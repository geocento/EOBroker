package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.ProductPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.ProductView;
import com.geocento.webapps.eobroker.admin.shared.dtos.EditProductDTO;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
public class ProductActivity extends TemplateActivity implements ProductView.Presenter {

    private ProductView productView;

    private EditProductDTO productDTO;

    public ProductActivity(ProductPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        productView = clientFactory.getProductView();
        productView.setPresenter(this);
        panel.setWidget(productView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        Long productId = null;
        if(tokens.containsKey(ProductPlace.TOKENS.id.toString())) {
            productId = Long.parseLong(tokens.get(ProductPlace.TOKENS.id.toString()));
        }
        if(productId != null) {
            // load all products
            try {
                REST.withCallback(new MethodCallback<EditProductDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, EditProductDTO response) {
                        setProduct(response);
                    }
                }).call(ServicesUtil.assetsService).getProduct(productId);
            } catch (RequestException e) {
            }
        } else {
            EditProductDTO editProductDTO = new EditProductDTO();
            setProduct(editProductDTO);
        }
    }

    private void setProduct(EditProductDTO editProductDTO) {
        this.productDTO = editProductDTO;
        productView.getName().setText(editProductDTO.getName());
        productView.setImageUrl(editProductDTO.getImageUrl());
        productView.getShortDescription().setText(editProductDTO.getShortDescription());
        productView.setDescription(editProductDTO.getDescription());
        productView.setSector(editProductDTO.getSector());
        productView.setThematic(editProductDTO.getThematic());
        productView.setGeoinformation(editProductDTO.getGeoinformation());
        productView.setPerformances(editProductDTO.getPerformances());
        productView.setFormFields(editProductDTO.getFormFields());
        productView.setAPIFields(editProductDTO.getApiFormFields());
        productView.setRecommendationRule(editProductDTO.getRecommendationRule());
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(productView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                productDTO.setName(productView.getName().getText());
                productDTO.setImageUrl(productView.getImageUrl());
                productDTO.setShortDescription(productView.getShortDescription().getText());
                productDTO.setDescription(productView.getDescription());
                productDTO.setSector(productView.getSector());
                productDTO.setThematic(productView.getThematic());
                productDTO.setGeoinformation(productView.getGeoinformation());
                productDTO.setPerformances(productView.getPerformances());
                productDTO.setFormFields(productView.getFormFields());
                productDTO.setApiFormFields(productView.getAPIFields());
                productDTO.setRecommendationRule(productView.getRecommendationRule());
                productView.setLoading("Saving product...");
                try {
                    REST.withCallback(new MethodCallback<Long>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            productView.setLoadingError("Error saving product");
                        }

                        @Override
                        public void onSuccess(Method method, Long productId) {
                            productView.hideLoading("Product saved");
                            productDTO.setId(productId);
                        }
                    }).call(ServicesUtil.assetsService).updateProduct(productDTO);
                } catch (RequestException e) {
                }
            }
        }));
    }

}
