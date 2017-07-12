package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.DataAccessUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.ProductDatasetPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.ProductDatasetView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDatasetDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductGeoinformation;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
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
        bind();
        displayFullLoading("Loading map...");
        productDatasetView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Error " + reason.getMessage());
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

        Long datasetId = null;
        if(tokens.containsKey(ProductDatasetPlace.TOKENS.id.toString())) {
            datasetId = Long.parseLong(tokens.get(ProductDatasetPlace.TOKENS.id.toString()));
        }
        if(datasetId != null) {
            // load dataset
            try {
                displayFullLoading("Loading off the shelf data information");
                REST.withCallback(new MethodCallback<ProductDatasetDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideFullLoading();
                        Window.alert("Failed to load off the shelf data information");
                    }

                    @Override
                    public void onSuccess(Method method, ProductDatasetDTO response) {
                        hideFullLoading();
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
        productDatasetView.setTitleLine(productDatasetDTO.getId() == null ? "Create off the shelf data product" : "Edit off the shelf data product");
        productDatasetView.getName().setText(productDatasetDTO.getName());
        productDatasetView.setIconUrl(productDatasetDTO.getImageUrl());
        productDatasetView.getDescription().setText(productDatasetDTO.getDescription());
        productDatasetView.setServiceType(productDatasetDTO.getServiceType());
        productDatasetView.setFullDescription(productDatasetDTO.getFullDescription());
        productDatasetView.setSelectedProduct(productDatasetDTO.getProduct());
        if(productDatasetDTO.getProduct() != null) {
            // add geoinformation
            productDatasetView.setProductGeoinformation(productDatasetDTO.getProductFeatures());
            productDatasetView.setSelectedGeoinformation(ListUtil.filterValues(productDatasetDTO.getProductFeatures(), new ListUtil.CheckValue<FeatureDescription>() {
                @Override
                public boolean isValue(FeatureDescription value) {
                    return productDatasetDTO.getSelectedFeatures().contains(value.getId());
                }
            }));
            productDatasetView.getGeoinformationComment().setText(productDatasetDTO.getGeoinformationComment());
            // add performances
            productDatasetView.setProductPerformances(productDatasetDTO.getPerformances());
            productDatasetView.setProvidedPerformances(productDatasetDTO.getProvidedPerformances());
            productDatasetView.getPerformancesComment().setText(productDatasetDTO.getPerformancesComment());
        } else {
            productDatasetView.setProductGeoinformation(null);
        }
        productDatasetView.setTemporalCoverage(productDatasetDTO.getTemporalCoverage() == null ? new TemporalCoverage() : productDatasetDTO.getTemporalCoverage());
        productDatasetView.getTemporalCoverageComment().setText(productDatasetDTO.getTemporalCoverageComment());
        productDatasetView.setExtent(AoIUtil.fromWKT(productDatasetDTO.getExtent()));
        productDatasetView.setCoverageLayers(productDatasetDTO.getCoverageLayers());
        productDatasetView.setDataAccess(productDatasetDTO.getDatasetAccesses());
        productDatasetView.setSampleDataAccess(productDatasetDTO.getSamples());
        productDatasetView.getDatasetStandard().setValue(productDatasetDTO.getDatasetStandard());
        productDatasetView.getDatasetURL().setText(productDatasetDTO.getDatasetURL());
/*
        productDatasetView.setProductGeoinformation(productDatasetDTO.getProductFeatures());
        productDatasetView.setSelectedGeoinformation(ListUtil.filterValues(productDatasetDTO.getProductFeatures(), new ListUtil.CheckValue<FeatureDescription>() {
            @Override
            public boolean isValue(FeatureDescription value) {
                return productDatasetDTO.getSelectedFeatures().contains(value.getId());
            }
        }));
*/
        productDatasetView.setSampleProductDatasetId(productDatasetDTO.getId());
        productDatasetView.setTermsAndConditions(productDatasetDTO.getTermsAndConditions());
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
                productDatasetDTO.setCoverageLayers(productDatasetView.getCoverageLayers());
                productDatasetDTO.setDatasetAccesses(productDatasetView.getDataAccesses());
                productDatasetDTO.setSamples(productDatasetView.getSamples());
                productDatasetDTO.setSelectedFeatures(ListUtil.mutate(productDatasetView.getSelectedGeoinformation(), new ListUtil.Mutate<FeatureDescription, Long>() {
                    @Override
                    public Long mutate(FeatureDescription featureDescription) {
                        return featureDescription.getId();
                    }
                }));
                productDatasetDTO.setGeoinformationComment(productDatasetView.getGeoinformationComment().getText());
                productDatasetDTO.setProvidedPerformances(productDatasetView.getSelectedPerformances());
                productDatasetDTO.setPerformancesComment(productDatasetView.getPerformancesComment().getText());
                productDatasetDTO.setTemporalCoverage(productDatasetView.getTemporalCoverage());
                productDatasetDTO.setTemporalCoverageComment(productDatasetView.getTemporalCoverageComment().getText());
                String datasetURL = productDatasetView.getDatasetURL().getText();
                if(datasetURL != null && datasetURL.length() > 0) {
                    productDatasetDTO.setDatasetStandard(productDatasetView.getDatasetStandard().getValue());
                    productDatasetDTO.setDatasetURL(datasetURL);
                } else {
                    productDatasetDTO.setDatasetStandard(null);
                    productDatasetDTO.setDatasetURL(null);
                }
                productDatasetDTO.setTermsAndConditions(productDatasetView.getTermsAndConditions());
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
                        throw new Exception("Please select a product category");
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
                        public void onSuccess(Method method, Long id) {
                            hideLoading();
                            displaySuccess("Off the shelf data product saved");
                            productDatasetDTO.setId(id);
                        }
                    }).call(ServicesUtil.assetsService).updateProductDataset(productDatasetDTO);
                } catch (RequestException e) {
                }
            }
        }));

        handlers.add(productDatasetView.getViewClient().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO - how do we get to use the place instead?
                Window.open(GWT.getHostPageBaseURL() + "#fullview:productdatasetid=" + productDatasetDTO.getId(), "_fullview;", null);
            }
        }));
    }

    @Override
    public void productChanged() {
        final ProductDTO selectedProduct = productDatasetView.getSelectedProduct();
        if(selectedProduct == null) {
            return;
        }
        displayLoading("Loading product geoinformation");
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
                    productDatasetView.setSelectedProduct(selectedProduct);
                    productDatasetView.setProductGeoinformation(productGeoinformation.getFeatureDescriptions());
                    productDatasetView.setProductPerformances(productGeoinformation.getPerformanceDescriptions());
                }

            }).call(ServicesUtil.assetsService).getProductGeoinformation(selectedProduct.getId());
        } catch (RequestException e) {
            e.printStackTrace();
        }
/*
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
                    productServiceView.setSelectedProduct(selectedProduct);
                    productServiceView.setProductGeoinformation(productGeoinformation.getFeatureDescriptions());
                    productServiceView.setProductPerformances(productGeoinformation.getPerformanceDescriptions());
                }

            }).call(ServicesUtil.assetsService).getProductGeoinformation(selectedProduct.getId());
        } catch (RequestException e) {
            e.printStackTrace();
        }
*/
    }

    @Override
    public void viewDataAccess(DatasetAccess datasetAccess) {
        if(datasetAccess instanceof DatasetAccessOGC) {
            if(datasetAccess.getId() == null) {
                Window.alert("Sorry, you need to save your off the shelf product first!");
                return;
            }
            Window.open(GWT.getHostPageBaseURL() + "#visualisation:" +
                    Utils.generateTokens("productDatasetId", productDatasetDTO.getId() + "",
                            "dataAccessId", datasetAccess.getId() + ""),
                            "_visualisation;", null);
        } else {
            if(datasetAccess.getUri() == null) {
                Window.alert("Sorry the URI parameter is not correct");
                return;
            }
            if(datasetAccess instanceof DatasetAccessFile) {
                Window.open(DataAccessUtils.getDownloadUrl((DatasetAccessFile) datasetAccess), "_blank", null);
            } else {
                Window.open(datasetAccess.getUri(), "_blank;", null);
            }
        }
    }
}
