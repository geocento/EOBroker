package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.ProductDatasetPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.ProductDatasetView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
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
import java.util.List;

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
            // load dataset
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

    private void setProductDataset(final ProductDatasetDTO productDatasetDTO) {
        this.productDatasetDTO = productDatasetDTO;
        productDatasetView.setTitleLine(productDatasetDTO.getId() == null ? "Create dataset" : "Edit dataset");
        productDatasetView.getName().setText(productDatasetDTO.getName());
        productDatasetView.setIconUrl(productDatasetDTO.getImageUrl());
        productDatasetView.getDescription().setText(productDatasetDTO.getDescription());
        productDatasetView.setServiceType(productDatasetDTO.getServiceType());
        productDatasetView.setFullDescription(productDatasetDTO.getFullDescription());
        productDatasetView.setSelectedProduct(productDatasetDTO.getProduct());
        productDatasetView.setExtent(AoIUtil.fromWKT(productDatasetDTO.getExtent()));
        productDatasetView.setDataAccess(productDatasetDTO.getDatasetAccesses());
        productDatasetView.setSampleDataAccess(productDatasetDTO.getSamples());
        productDatasetView.setProductGeoinformation(productDatasetDTO.getProductFeatures());
        productDatasetView.setSelectedGeoinformation(ListUtil.filterValues(productDatasetDTO.getProductFeatures(), new ListUtil.CheckValue<FeatureDescription>() {
            @Override
            public boolean isValue(FeatureDescription value) {
                return productDatasetDTO.getSelectedFeatures().contains(value.getId());
            }
        }));
        productDatasetView.setSampleProductDatasetId(productDatasetDTO.getId());
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
                productDatasetDTO.setServiceType(productDatasetView.getServiceType());
                productDatasetDTO.setProduct(productDatasetView.getSelectedProduct());
                productDatasetDTO.setExtent(productDatasetView.getExtent() == null ? null : AoIUtil.toWKT(productDatasetView.getExtent()));
                productDatasetDTO.setDatasetAccesses(productDatasetView.getDataAccesses());
                productDatasetDTO.setSamples(productDatasetView.getSamples());
                productDatasetDTO.setSelectedFeatures(ListUtil.mutate(productDatasetView.getSelectedGeoinformation(), new ListUtil.Mutate<FeatureDescription, Long>() {
                    @Override
                    public Long mutate(FeatureDescription featureDescription) {
                        return featureDescription.getId();
                    }
                }));
                // do some checks
                try {
                    if (productDatasetDTO.getName() == null || productDatasetDTO.getName().length() < 3) {
                        throw new Exception("Please provide a valid name");
                    }
                    if (productDatasetDTO.getImageUrl() == null) {
                        throw new Exception("Please provide a picture");
                    }
                    if (productDatasetDTO.getDescription() == null || productDatasetDTO.getDescription().length() < 3) {
                        throw new Exception("Please provide a valid description");
                    }
                    if (productDatasetDTO.getFullDescription() == null || productDatasetDTO.getFullDescription().length() < 3) {
                        throw new Exception("Please provide a valid full description");
                    }
                    if (productDatasetDTO.getProduct() == null) {
                        throw new Exception("Please select a product");
                    }
                    if (productDatasetDTO.getServiceType() == null) {
                        throw new Exception("Please provide a service type");
                    }
                } catch (Exception e) {
                    // TODO - use the view instead
                    Window.alert(e.getMessage());
                    return;
                }
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

    @Override
    public void productChanged() {
        displayLoading("Loading product geoinformation");
        ProductDTO selectedProduct = productDatasetView.getSelectedProduct();
        try {
            REST.withCallback(new MethodCallback<List<FeatureDescription>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideLoading();
                    displayError("Could not load product geoinformation");
                }

                @Override
                public void onSuccess(Method method, List<FeatureDescription> featureDescriptions) {
                    hideLoading();
                    productDatasetView.setProductGeoinformation(featureDescriptions);
                }

            }).call(ServicesUtil.assetsService).getProductGeoinformation(selectedProduct.getId());
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }
}
