package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.events.RequestCreated;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.places.ProductFormPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.ProductFormView;
import com.geocento.webapps.eobroker.customer.shared.ProductFormDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceDTO;
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
        setTemplateView(productFormView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        productFormView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Error " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                setAoI(currentAoI);
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
            List<Long> productServiceIds = ListUtil.mutate(productFormView.getSelectedServices(), new ListUtil.Mutate<ProductServiceDTO, Long>() {
                @Override
                public Long mutate(ProductServiceDTO productServiceDTO) {
                    return productServiceDTO.getId();
                }
            });
            if(productServiceIds.size() == 0) {
                throw new Exception("Please select at least one service");
            }
            productFormView.displayLoading("Submitting requests");
            ProductServiceRequestDTO productServiceRequestDTO = new ProductServiceRequestDTO();
            productServiceRequestDTO.setProductId(productId);
            if(currentAoI == null) {
                throw new Exception("Please define an AoI first");
            }
            productServiceRequestDTO.setAoIWKT(AoIUtil.toWKT(currentAoI));
            productServiceRequestDTO.setProductServiceIds(productServiceIds);
            productServiceRequestDTO.setValues(values);
            try {
                REST.withCallback(new MethodCallback<RequestDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        productFormView.hideLoading();
                        productFormView.displayError(exception.getMessage());
                    }

                    @Override
                    public void onSuccess(Method method, RequestDTO requestDTO) {
                        activityEventBus.fireEvent(new RequestCreated(requestDTO));
                        productFormView.hideLoading();
                        productFormView.displaySubmittedSuccess("Your request has been successfully submitted");
                    }
                }).call(ServicesUtil.ordersService).submitProductRequest(productServiceRequestDTO);
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
        if(productId == null) {
            Window.alert("Product id is required");
            clientFactory.getPlaceController().goTo(clientFactory.getDefaultPlace());
            return;
        }

        productFormView.displayLoading();
        try {
            REST.withCallback(new MethodCallback<ProductFormDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    productFormView.hideLoading();
                    productFormView.displayError("Could not retrieve product information");
                }

                @Override
                public void onSuccess(Method method, final ProductFormDTO productFormDTO) {
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
                        productFormView.addProductService(productServiceDTO);
                    }
                    handlers.add(productFormView.getInformation().addClickHandler(new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                            Window.open("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.productid.toString() + "=" + productFormDTO.getId())), "_blank", null);
                        }
                    }));

                }
            }).call(ServicesUtil.assetsService).getProductForm(productId);
        } catch (RequestException e) {
        }

    }

    public void setAoI(AoIDTO aoi) {
        super.setAoI(aoi);
        productFormView.displayAoI(aoi);
    }

}
