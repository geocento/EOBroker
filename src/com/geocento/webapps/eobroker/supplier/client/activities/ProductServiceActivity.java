package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.supplier.client.places.ProductServicePlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.ProductServiceView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductGeoinformation;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceEditDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductServiceActivity extends TemplateActivity implements ProductServiceView.Presenter {

    private ProductServiceView productServiceView;

    private ProductServiceEditDTO productServiceDTO;

    public ProductServiceActivity(ProductServicePlace place, ClientFactory clientFactory) {
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
        displayFullLoading("Loading map...");
        productServiceView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Problem loading map...");
                hideFullLoading();
                handleHistory();
            }

            @Override
            public void onSuccess(Void result) {
                hideFullLoading();
                handleHistory();
            }
        });
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        Long serviceId = null;
        if(tokens.containsKey(ProductServicePlace.TOKENS.service.toString())) {
            try {
                serviceId = Long.parseLong(tokens.get(ProductServicePlace.TOKENS.service.toString()));
            } catch (Exception e) {

            }
        }

        if(serviceId != null) {
            displayFullLoading("Loading on demand service...");
            productServiceView.setTitleLine("Edit your on demand service details and settings");
            try {
                REST.withCallback(new MethodCallback<ProductServiceEditDTO>() {

                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideFullLoading();
                        Window.alert("Problem loading on demand service");
                    }

                    @Override
                    public void onSuccess(Method method, ProductServiceEditDTO productServiceDTO) {
                        hideFullLoading();
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
        if(productServiceDTO.getProduct() != null) {
            // add geoinformation
            productServiceView.setProductGeoinformation(productServiceDTO.getProductFeatures());
            productServiceView.setSelectedGeoinformation(ListUtil.filterValues(productServiceDTO.getProductFeatures(), new ListUtil.CheckValue<FeatureDescription>() {
                @Override
                public boolean isValue(FeatureDescription value) {
                    return productServiceDTO.getSelectedFeatures().contains(value.getId());
                }
            }));
            productServiceView.getGeoinformationComment().setText(productServiceDTO.getGeoinformationComment());
            // add performances
            productServiceView.setProductPerformances(productServiceDTO.getPerformances());
            productServiceView.setProvidedPerformances(productServiceDTO.getProvidedPerformances());
            productServiceView.getPerformancesComment().setText(productServiceDTO.getPerformancesComment());
        } else {
            productServiceView.setProductGeoinformation(null);
        }
        productServiceView.setExtent(AoIUtil.fromWKT(productServiceDTO.getExtent()));
        productServiceView.setCoverageLayers(productServiceDTO.getCoverageLayers());
        productServiceView.getWebsite().setText(productServiceDTO.getWebsite());
        productServiceView.getAPIUrl().setText(productServiceDTO.getApiURL() == null ? "" : productServiceDTO.getApiURL());
        productServiceView.setSelectedDataAccessTypes(productServiceDTO.getSelectedDataAccessTypes());
        productServiceView.getDisseminationComment().setText(productServiceDTO.getDisseminationComment());
        productServiceView.getDeliveryTime().setText(productServiceDTO.getTimeToDelivery());
        productServiceView.setSampleDataAccess(productServiceDTO.getSamples());
        // needed for the upload of samples on the broker server
        productServiceView.setSampleProductServiceId(productServiceDTO.getId());
        productServiceView.setTermsAndConditions(productServiceDTO.getTermsAndConditions());
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
                productServiceDTO.setGeoinformationComment(productServiceView.getGeoinformationComment().getText());
                productServiceDTO.setProvidedPerformances(productServiceView.getSelectedPerformances());
                productServiceDTO.setPerformancesComment(productServiceView.getPerformancesComment().getText());
                productServiceDTO.setExtent(productServiceView.getExtent() == null ? null : AoIUtil.toWKT(productServiceView.getExtent()));
                productServiceDTO.setCoverageLayers(productServiceView.getCoverageLayers());
                productServiceDTO.setEmail(productServiceView.getEmail().getText());
                productServiceDTO.setWebsite(productServiceView.getWebsite().getText());
                productServiceDTO.setFullDescription(productServiceView.getFullDescription());
                productServiceDTO.setApiURL(productServiceView.getAPIUrl().getText().length() > 0 ? productServiceView.getAPIUrl().getText() : null);
                productServiceDTO.setSelectedDataAccessTypes(productServiceView.getSelectedDataAccessTypes());
                productServiceDTO.setDisseminationComment(productServiceView.getDisseminationComment().getText());
                productServiceDTO.setTimeToDelivery(productServiceView.getDeliveryTime().getText());
                productServiceDTO.setSamples(productServiceView.getSamples());
                productServiceDTO.setTermsAndConditions(productServiceView.getTermsAndConditions());
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
                    REST.withCallback(new MethodCallback<Long>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError("Could not save product service");
                        }

                        @Override
                        public void onSuccess(Method method, Long productId) {
                            hideLoading();
                            displaySuccess("Product service saved");
                            productServiceDTO.setId(productId);
                            History.newItem(PlaceHistoryHelper.convertPlace(new ProductServicePlace(Utils.generateTokens(ProductServicePlace.TOKENS.service.toString(), productId.toString()))));
                        }

                    }).call(ServicesUtil.assetsService).updateProductService(productServiceDTO);
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));

        handlers.add(productServiceView.getViewClient().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO - how do we get to use the place instead?
                Window.open(GWT.getHostPageBaseURL() + "#fullview:productserviceid=" + productServiceDTO.getId(), "_fullview;", null);
            }
        }));
    }

    @Override
    public void productChanged() {
        displayLoading("Loading product geoinformation");
        // TODO - load the geoinformation and the performances and update the display
        final ProductDTO selectedProduct = productServiceView.getSelectedProduct();
        try {
            REST.withCallback(new MethodCallback<ProductGeoinformation>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideLoading();
                    displayError("Could not load product geoinformation");
                }

                @Override
                public void onSuccess(Method method, ProductGeoinformation productGeoinformation) {
                    hideLoading();
                    productServiceView.setSelectedProduct(selectedProduct);
                    productServiceView.setProductGeoinformation(productGeoinformation.getFeatureDescriptions());
                    productServiceView.setProductPerformances(productGeoinformation.getPerformanceDescriptions());
                }

            }).call(ServicesUtil.assetsService).getProductGeoinformation(selectedProduct.getId());
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }
}
