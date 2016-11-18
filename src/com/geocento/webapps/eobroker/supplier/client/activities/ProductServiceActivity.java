package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.ServicesPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.ProductServiceView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceEditDTO;
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
public class ProductServiceActivity extends TemplateActivity implements ProductServiceView.Presenter {

    private ProductServiceView productServiceView;

    private ProductServiceEditDTO productServiceDTO;

    public ProductServiceActivity(ServicesPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        productServiceView = clientFactory.getProductServiceView();
        productServiceView.setPresenter(this);
        setTemplateView(productServiceView.getTemplateView());
        panel.setWidget(productServiceView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        productServiceView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {

            }

            @Override
            public void onSuccess(Void result) {
                handleHistory();
            }
        });
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        Long serviceId = null;
        if(tokens.containsKey(ServicesPlace.TOKENS.service.toString())) {
            try {
                serviceId = Long.parseLong(tokens.get(ServicesPlace.TOKENS.service.toString()));
            } catch (Exception e) {

            }
        }

        if(serviceId != null) {
            productServiceView.setTitleLine("Edit your services details and settings");
            try {
                REST.withCallback(new MethodCallback<ProductServiceEditDTO>() {

                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, ProductServiceEditDTO productServiceDTO) {
                        setService(productServiceDTO);
                    }

                }).call(ServicesUtil.assetsService).getProductService(serviceId);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } else {
            productServiceView.setTitleLine("Create new service");
            ProductServiceEditDTO productServiceDTO = new ProductServiceEditDTO();
            setService(productServiceDTO);
        }

    }

    private void setService(final ProductServiceEditDTO productServiceDTO) {
        ProductServiceActivity.this.productServiceDTO = productServiceDTO;
        productServiceView.getName().setText(productServiceDTO.getName());
        productServiceView.setIconUrl(productServiceDTO.getServiceImage());
        productServiceView.getDescription().setText(productServiceDTO.getDescription());
        productServiceView.setFullDescription(productServiceDTO.getFullDescription());
        productServiceView.getEmail().setText(productServiceDTO.getEmail());
        productServiceView.setSelectedProduct(productServiceDTO.getProduct());
        productServiceView.setProductGeoinformation(productServiceDTO.getProductFeatures());
        productServiceView.setSelectedGeoinformation(ListUtil.filterValues(productServiceDTO.getProductFeatures(), new ListUtil.CheckValue<FeatureDescription>() {
            @Override
            public boolean isValue(FeatureDescription value) {
                return productServiceDTO.getSelectedFeatures().contains(value.getId());
            }
        }));
        productServiceView.setExtent(AoIUtil.fromWKT(productServiceDTO.getExtent()));
        productServiceView.getWebsite().setText(productServiceDTO.getWebsite());
        productServiceView.getAPIUrl().setText(productServiceDTO.getApiURL() == null ? "" : productServiceDTO.getApiURL());
        productServiceView.setSampleDataAccess(productServiceDTO.getSamples());
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(productServiceView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                productServiceDTO.setName(productServiceView.getName().getText());
                productServiceDTO.setDescription(productServiceView.getDescription().getText());
                productServiceDTO.setServiceImage(productServiceView.getIconUrl());
                productServiceDTO.setProduct(productServiceView.getSelectedProduct());
                productServiceDTO.setSelectedFeatures(ListUtil.mutate(productServiceView.getSelectedGeoinformation(), new ListUtil.Mutate<FeatureDescription, Long>() {
                    @Override
                    public Long mutate(FeatureDescription featureDescription) {
                        return featureDescription.getId();
                    }
                }));
                productServiceDTO.setExtent(AoIUtil.toWKT(productServiceView.getExtent()));
                productServiceDTO.setEmail(productServiceView.getEmail().getText());
                productServiceDTO.setWebsite(productServiceView.getWebsite().getText());
                productServiceDTO.setFullDescription(productServiceView.getFullDescription());
                productServiceDTO.setApiURL(productServiceView.getAPIUrl().getText().length() > 0 ? productServiceView.getAPIUrl().getText() : null);
                productServiceDTO.setSamples(productServiceView.getSamples());
                // do some checks
                try {
                    if (productServiceDTO.getName() == null || productServiceDTO.getName().length() < 3) {
                        throw new Exception("Please provide a valid name");
                    }
                    if (productServiceDTO.getServiceImage() == null) {
                        throw new Exception("Please provide a picture");
                    }
                    if (productServiceDTO.getDescription() == null || productServiceDTO.getDescription().length() < 3) {
                        throw new Exception("Please provide a valid description");
                    }
                    if (productServiceDTO.getFullDescription() == null || productServiceDTO.getFullDescription().length() < 3) {
                        throw new Exception("Please provide a valid full description");
                    }
                    if (productServiceDTO.getProduct() == null) {
                        throw new Exception("Please select a product");
                    }
                } catch (Exception e) {
                    // TODO - use the view instead
                    Window.alert(e.getMessage());
                    return;
                }
                displayLoading("Saving product service");
                try {
                    REST.withCallback(new MethodCallback<Void>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError("Could not save product service");
                        }

                        @Override
                        public void onSuccess(Method method, Void response) {
                            hideLoading();
                            displaySuccess("Product service saved");
                        }

                    }).call(ServicesUtil.assetsService).updateProductService(productServiceDTO);
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    @Override
    public void productChanged() {
        displayLoading("Loading product geoinformation");
        ProductDTO selectedProduct = productServiceView.getSelectedProduct();
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
                    productServiceView.setProductGeoinformation(featureDescriptions);
                }

            }).call(ServicesUtil.assetsService).getProductGeoinformation(selectedProduct.getId());
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }
}
