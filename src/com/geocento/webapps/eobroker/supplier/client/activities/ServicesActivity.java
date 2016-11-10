package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.ServicesPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.ServicesView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceEditDTO;
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
public class ServicesActivity extends TemplateActivity implements ServicesView.Presenter {

    private ServicesView servicesView;

    private ProductServiceEditDTO productServiceDTO;

    public ServicesActivity(ServicesPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        servicesView = clientFactory.getServicesView();
        servicesView.setPresenter(this);
        setTemplateView(servicesView.getTemplateView());
        panel.setWidget(servicesView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
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
            servicesView.setTitleLine("Edit your services details and settings");
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
            servicesView.setTitleLine("Create new service");
            ProductServiceEditDTO productServiceDTO = new ProductServiceEditDTO();
            setService(productServiceDTO);
        }

    }

    private void setService(ProductServiceEditDTO productServiceDTO) {
        ServicesActivity.this.productServiceDTO = productServiceDTO;
        servicesView.getName().setText(productServiceDTO.getName());
        servicesView.getDescription().setText(productServiceDTO.getDescription());
        servicesView.setIconUrl(productServiceDTO.getServiceImage());
        servicesView.getEmail().setText(productServiceDTO.getEmail());
        servicesView.getWebsite().setText(productServiceDTO.getWebsite());
        servicesView.setSelectedProduct(productServiceDTO.getProduct());
        servicesView.setFullDescription(productServiceDTO.getFullDescription());
        servicesView.getAPIUrl().setText(productServiceDTO.getApiURL() == null ? "" : productServiceDTO.getApiURL());
        servicesView.getSampleWmsUrl().setText(productServiceDTO.getSampleWmsUrl() == null ? "" : productServiceDTO.getSampleWmsUrl());
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(servicesView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                productServiceDTO.setName(servicesView.getName().getText());
                productServiceDTO.setDescription(servicesView.getDescription().getText());
                productServiceDTO.setServiceImage(servicesView.getIconUrl());
                productServiceDTO.setProduct(servicesView.getSelectProduct());
                productServiceDTO.setEmail(servicesView.getEmail().getText());
                productServiceDTO.setWebsite(servicesView.getWebsite().getText());
                productServiceDTO.setFullDescription(servicesView.getFullDescription());
                productServiceDTO.setApiURL(servicesView.getAPIUrl().getText().length() > 0 ? servicesView.getAPIUrl().getText() : null);
                productServiceDTO.setSampleWmsUrl(servicesView.getSampleWmsUrl().getText().length() > 0 ? servicesView.getSampleWmsUrl().getText() : null);
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

}
