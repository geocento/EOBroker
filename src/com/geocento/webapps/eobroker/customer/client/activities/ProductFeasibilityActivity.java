package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.ProductFeasibilityPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.ProductFeasibilityView;
import com.geocento.webapps.eobroker.customer.shared.FeasibilityRequestDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductFeasibilityDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceFeasibilityDTO;
import com.geocento.webapps.eobroker.customer.shared.SupplierAPIResponse;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gwt.material.design.client.ui.MaterialToast;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductFeasibilityActivity extends TemplateActivity implements ProductFeasibilityView.Presenter {

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
/*
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
*/
        Long productServiceId = null;
        if (tokens.containsKey(ProductFeasibilityPlace.TOKENS.productservice.toString())) {
            try {
                productServiceId = Long.parseLong(tokens.get(ProductFeasibilityPlace.TOKENS.productservice.toString()));
            } catch (Exception e) {

            }
        }
        loadProduct(productServiceId);
        setAoI(Customer.currentAoI);
    }

    private void setAoI(AoI aoi) {
        this.aoi = aoi;
        productFeasibilityView.displayAoI(Customer.currentAoI);
    }

    private void loadProduct(final Long productServiceId) {
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
                    List<FormElement> formElements = new ArrayList<FormElement>();
                    formElements.addAll(response.getApiFormElements());
                    productFeasibilityView.setFormElements(formElements);
                    if(productServiceId != null) {
                        selectService(ListUtil.findValue(response.getProductServices(), new ListUtil.CheckValue<ProductServiceFeasibilityDTO>() {
                            @Override
                            public boolean isValue(ProductServiceFeasibilityDTO value) {
                                return value.getId().longValue() == productServiceId.longValue();
                            }
                        }));
                    }
                }
            }).call(ServicesUtil.assetsService).getProductFeasibility(productServiceId);
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
        super.bind();
        handlers.add(productFeasibilityView.getUpdateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                productFeasibilityView.clearResults();
                productFeasibilityView.displayLoadingResults("Checking feasibility...");
                // create request
                FeasibilityRequestDTO feasibilityRequestDTO = new FeasibilityRequestDTO();
                feasibilityRequestDTO.setProductServiceId(productFeasibilityService.getId());
                feasibilityRequestDTO.setAoiWKT(AoIUtil.toWKT(aoi));
                feasibilityRequestDTO.setStart(start);
                feasibilityRequestDTO.setStop(stop);
                feasibilityRequestDTO.setFormElementValues(formElementValues);
                try {
                    REST.withCallback(new MethodCallback<SupplierAPIResponse>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            productFeasibilityView.hideLoadingResults();
                            productFeasibilityView.displayResultsError(exception.getMessage());
                        }

                        @Override
                        public void onSuccess(Method method, SupplierAPIResponse response) {
                            productFeasibilityView.hideLoadingResults();
                            productFeasibilityView.displayResponse(response);
                        }
                    }).call(ServicesUtil.searchService).callSupplierAPI(feasibilityRequestDTO);
                } catch (Exception e) {

                }
            }
        }));
    }

    @Override
    public void aoiChanged(AoI aoi) {
        setAoI(aoi);
        enableUpdateMaybe();
    }

    private void enableUpdateMaybe() {
        if(aoi == null) {
            MaterialToast.fireToast("Please select AoI");
            enableUpdate(false);
            return;
        }
        if(start == null || stop == null) {
            MaterialToast.fireToast("Please select start and stop dates");
            enableUpdate(false);
            return;
        }
        enableUpdate(true);
    }

    private void enableUpdate(boolean enable) {
        productFeasibilityView.enableUpdate(enable);
    }

    @Override
    public void onServiceChanged(ProductServiceFeasibilityDTO productServiceFeasibilityDTO) {
        this.productFeasibilityService = productServiceFeasibilityDTO;
        enableUpdateMaybe();
    }

    @Override
    public void onStartDateChanged(Date start) {
        this.start = start;
        enableUpdateMaybe();
    }

    @Override
    public void onStopDateChanged(Date stop) {
        this.stop = stop;
        enableUpdateMaybe();
    }

    @Override
    public void onFormElementChanged(final FormElement formElement) {
        // needs updating?
/*
        ListUtil.findValue(formElementValues, new ListUtil.CheckValue<FormElementValue>() {
            @Override
            public boolean isValue(FormElementValue value) {
                return formElement.getFormid().contentEquals(value.getFormid());
            }
        });
*/
        enableUpdateMaybe();
    }
}
