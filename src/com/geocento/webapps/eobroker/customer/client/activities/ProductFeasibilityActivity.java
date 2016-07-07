package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.SupplierAPIResponse;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.ProductFeasibilityPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.ProductFeasibilityView;
import com.geocento.webapps.eobroker.customer.shared.ProductFeasibilityDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceFeasibilityDTO;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductFeasibilityActivity extends AbstractApplicationActivity implements ProductFeasibilityView.Presenter {

    private ProductFeasibilityView productFeasibilityView;

    private Date start;
    private Date stop;
    private List<FormElementValue> formElementValues;
    private ProductServiceFeasibilityDTO productFeasibilityService;
    private AoI aoi;

    public ProductFeasibilityActivity(ProductFeasibilityPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        productFeasibilityView = clientFactory.getProductFeasibilityView();
        productFeasibilityView.setPresenter(this);
        panel.setWidget(productFeasibilityView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        productFeasibilityView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Error " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                handleHistory();
            }
        });
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        Long productId = null;
        if (tokens.containsKey(ProductFeasibilityPlace.TOKENS.product.toString())) {
            try {
                productId = Long.parseLong(tokens.get(ProductFeasibilityPlace.TOKENS.product.toString()));
            } catch (Exception e) {

            }
        }
        if(productId == null) {
            Window.alert("Product id cannot be null");
            clientFactory.getPlaceController().goTo(clientFactory.getDefaultPlace());
        }
        Long productServiceId = null;
        if (tokens.containsKey(ProductFeasibilityPlace.TOKENS.productservice.toString())) {
            try {
                productServiceId = Long.parseLong(tokens.get(ProductFeasibilityPlace.TOKENS.productservice.toString()));
            } catch (Exception e) {

            }
        }
        loadProduct(productId, productServiceId);
        productFeasibilityView.displayAoI(Customer.currentAoI);
    }

    private void loadProduct(Long productId, final Long productServiceId) {
        productFeasibilityView.displayLoading("Loading product...");
        try {
            REST.withCallback(new MethodCallback<ProductFeasibilityDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    productFeasibilityView.hideLoading();
                    productFeasibilityView.displayError(exception.getMessage());
                }

                @Override
                public void onSuccess(Method method, ProductFeasibilityDTO response) {
                    productFeasibilityView.hideLoading();
                    productFeasibilityView.setServices(response.getProductServices());
                    productFeasibilityView.setFormElements(response.getApiFormElements());
                    if(productServiceId != null) {
                        selectService(ListUtil.findValue(response.getProductServices(), new ListUtil.CheckValue<ProductServiceFeasibilityDTO>() {
                            @Override
                            public boolean isValue(ProductServiceFeasibilityDTO value) {
                                return value.getId().longValue() == productServiceId.longValue();
                            }
                        }));
                    }
                }
            }).call(ServicesUtil.assetsService).getProductFeasibility(productId);
        } catch (RequestException e) {
        }
    }

    private void selectService(ProductServiceFeasibilityDTO productServiceFeasibilityDTO) {
        this.productFeasibilityService = productServiceFeasibilityDTO;
        productFeasibilityView.selectService(productServiceFeasibilityDTO);
    }

    public void setStart(Date start) {
        this.start = start;
        productFeasibilityView.setStart(start);
    }

    public void setStop(Date stop) {
        this.stop = stop;
        productFeasibilityView.setStop(stop);
    }

    public void setFormElementValues(List<FormElementValue> formElementValues) {
        this.formElementValues = formElementValues;
        productFeasibilityView.setFormElementValues(formElementValues);
    }

    @Override
    protected void bind() {
        handlers.add(productFeasibilityView.getUpdateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                productFeasibilityView.displayLoadingResults("Checking feasibility...");
                try {
                    REST.withCallback(new MethodCallback<SupplierAPIResponse>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {

                        }

                        @Override
                        public void onSuccess(Method method, SupplierAPIResponse response) {

                        }
                    }).call(ServicesUtil.searchService).callSupplierAPI(productFeasibilityService.getId(), aoi.getId(), start, stop, formElementValues);
                } catch (Exception e) {

                }
            }
        }));
    }

    @Override
    public void aoiChanged(AoI aoi) {
        this.aoi = aoi;
    }

    @Override
    public void onServiceChanged(ProductServiceFeasibilityDTO productServiceFeasibilityDTO) {
        this.productFeasibilityService = productServiceFeasibilityDTO;
    }

    @Override
    public void onStartDateChanged(Date start) {
        this.start = start;
    }

    @Override
    public void onStopDateChanged(Date stop) {
        this.stop = stop;
    }

    @Override
    public void onFormElementChanged(FormElement formElement) {

    }
}
