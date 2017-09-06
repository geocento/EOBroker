package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.entities.requests.RequestDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.events.RequestCreated;
import com.geocento.webapps.eobroker.customer.client.places.ProductFormPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.ProductFormView;
import com.geocento.webapps.eobroker.customer.shared.ProductFormDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceFormDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceRequestDTO;
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
public class ProductFormActivity extends TemplateActivity implements ProductFormView.Presenter {

    private ProductFormView productFormView;

    private Long productId;
    private Long productServiceId;

    public ProductFormActivity(ProductFormPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        productFormView = clientFactory.getProductFormView();
        productFormView.setPresenter(this);
        setTemplateView(productFormView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        displayFullLoading("Loading map...");
        productFormView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Failed to load map");
            }

            @Override
            public void onSuccess(Void result) {
                setAoI(currentAoI);
                hideFullLoading();
                handleHistory();
            }
        });
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(productFormView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                submitForm();
            }
        }));

    }

    private void submitForm() {
        try {
            List<FormElementValue> values = productFormView.getFormElementValues();
            ProductServiceRequestDTO productServiceRequestDTO = new ProductServiceRequestDTO();
            productServiceRequestDTO.setProductId(productId);
            if(currentAoI == null) {
                throw new Exception("Please define an AoI first");
            }
            productServiceRequestDTO.setAoIWKT(AoIUtil.toWKT(currentAoI));
            if(productServiceId == null) {
                List<Long> productServiceIds = ListUtil.mutate(productFormView.getSelectedServices(), new ListUtil.Mutate<ProductServiceDTO, Long>() {
                    @Override
                    public Long mutate(ProductServiceDTO productServiceDTO) {
                        return productServiceDTO.getId();
                    }
                });
                if (productServiceIds.size() == 0) {
                    throw new Exception("Please select at least one service");
                }
                productServiceRequestDTO.setProductServiceIds(productServiceIds);
            } else {
                productServiceRequestDTO.setProductServiceIds(ListUtil.toList(productServiceId));
            }
            productServiceRequestDTO.setValues(values);
            displayLoading();
            try {
                REST.withCallback(new MethodCallback<RequestDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideLoading();
                        displayError(method.getResponse().getText());
                    }

                    @Override
                    public void onSuccess(Method method, RequestDTO requestDTO) {
                        activityEventBus.fireEvent(new RequestCreated(requestDTO));
                        hideLoading();
                        displaySuccess("Your request has been successfully submitted");
                        clearRequest();
                    }
                }).call(ServicesUtil.requestsService).submitProductRequest(productServiceRequestDTO);
            } catch (Exception e) {
            }
        } catch (Exception e) {
            productFormView.displayFormValidationError(e.getMessage());
        }
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        productId = null;
        if(tokens.containsKey(ProductFormPlace.TOKENS.id.toString())) {
            try {
                productId = Long.parseLong(tokens.get(ProductFormPlace.TOKENS.id.toString()));
            } catch (Exception e) {

            }
        }
        productServiceId = null;
        if(tokens.containsKey(ProductFormPlace.TOKENS.serviceid.toString())) {
            try {
                productServiceId = Long.parseLong(tokens.get(ProductFormPlace.TOKENS.serviceid.toString()));
            } catch (Exception e) {

            }
        }
        if(productId == null && productServiceId == null) {
            Window.alert("At least product or bespoke service is required");
            clientFactory.getPlaceController().goTo(clientFactory.getDefaultPlace());
            return;
        }

        clearRequest();
        displayLoading();
        if(productId != null) {
            try {
                REST.withCallback(new MethodCallback<ProductFormDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideLoading();
                        displayError("Could not retrieve product information");
                    }

                    @Override
                    public void onSuccess(Method method, final ProductFormDTO productFormDTO) {
                        hideLoading();
                        productFormView.setProduct(productFormDTO);
                        clearRequest();
                    }
                }).call(ServicesUtil.assetsService).getProductForm(productId);
            } catch (RequestException e) {
            }
        } else {
            try {
                REST.withCallback(new MethodCallback<ProductServiceFormDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideLoading();
                        displayError("Could not retrieve product information");
                    }

                    @Override
                    public void onSuccess(Method method, final ProductServiceFormDTO productServiceFormDTO) {
                        hideLoading();
                        // make sure we also update the product id
                        productId = productServiceFormDTO.getProduct().getId();
                        productFormView.setProductService(productServiceFormDTO);
                        clearRequest();
                    }
                }).call(ServicesUtil.assetsService).getProductServiceForm(productServiceId);
            } catch (RequestException e) {
            }
        }

    }

    private void clearRequest() {
        productFormView.clearRequest();
    }

    public void setAoI(AoIDTO aoi) {
        super.setAoI(aoi);
        productFormView.displayAoI(aoi);
    }

}
