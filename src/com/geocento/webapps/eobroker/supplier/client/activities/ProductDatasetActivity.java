package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.ProductDatasetPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.ProductDatasetView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDatasetDTO;
import com.google.gwt.core.client.Callback;
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
public class ProductDatasetActivity extends TemplateActivity implements ProductDatasetView.Presenter {

    private ProductDatasetView productDatasetView;

    private ProductDatasetDTO productDatasetDTO;

    public ProductDatasetActivity(ProductDatasetPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        productDatasetView = clientFactory.getProductDatasetView();
        productDatasetView.setPresenter(this);
        setTemplateView(productDatasetView.getTemplateView());
        panel.setWidget(productDatasetView.asWidget());
        Window.setTitle("Earth Observation Broker");
        productDatasetView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Error " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                bind();
                handleHistory();
            }
        });
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        Long datasetId = null;
        if(tokens.containsKey(ProductDatasetPlace.TOKENS.id.toString())) {
            datasetId = Long.parseLong(tokens.get(ProductDatasetPlace.TOKENS.id.toString()));
        }
        if(datasetId != null) {
            // load all companys
            try {
                REST.withCallback(new MethodCallback<ProductDatasetDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, ProductDatasetDTO response) {
                        setProductDataset(response);
                    }
                }).call(ServicesUtil.assetsService).getProductDataset(datasetId);
            } catch (RequestException e) {
            }
        } else {
            ProductDatasetDTO company = new ProductDatasetDTO();
            setProductDataset(company);
        }
    }

    private void setProductDataset(ProductDatasetDTO productDatasetDTO) {
        this.productDatasetDTO = productDatasetDTO;
        productDatasetView.setTitleLine(productDatasetDTO.getId() == null ? "Create dataset" : "Edit dataset");
        productDatasetView.getName().setText(productDatasetDTO.getName());
        productDatasetView.setIconUrl(productDatasetDTO.getImageUrl());
        productDatasetView.getDescription().setText(productDatasetDTO.getDescription());
        productDatasetView.setFullDescription(productDatasetDTO.getFullDescription());
        productDatasetView.setSelectedProduct(productDatasetDTO.getProduct());
        productDatasetView.setExtent(AoIUtil.fromWKT(productDatasetDTO.getExtent()));
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(productDatasetView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                productDatasetDTO.setName(productDatasetView.getName().getText());
                productDatasetDTO.setImageUrl(productDatasetView.getImageUrl());
                productDatasetDTO.setDescription(productDatasetView.getDescription().getText());
                productDatasetDTO.setFullDescription(productDatasetView.getFullDescription());
                productDatasetDTO.setProduct(productDatasetView.getSelectProduct());
                productDatasetDTO.setExtent(AoIUtil.toWKT(productDatasetView.getExtent()));
                displayLoading("Saving dataset...");
                try {
                    REST.withCallback(new MethodCallback<Long>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError("Error saving dataset");
                        }

                        @Override
                        public void onSuccess(Method method, Long companyId) {
                            hideLoading();
                            displaySuccess("Product dataset saved");
                            productDatasetDTO.setId(companyId);
                        }
                    }).call(ServicesUtil.assetsService).saveProductDataset(productDatasetDTO);
                } catch (RequestException e) {
                }
            }
        }));
    }

}
