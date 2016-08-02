package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.events.SearchImagery;
import com.geocento.webapps.eobroker.customer.client.events.SearchImageryHandler;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.ImageSearchPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.FullView;
import com.geocento.webapps.eobroker.customer.shared.CompanyDescriptionDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductDescriptionDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceDescriptionDTO;
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
public class FullViewActivity extends TemplateActivity implements FullView.Presenter {

    private FullView fullView;

    public FullViewActivity(FullViewPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        fullView = clientFactory.getFullView();
        fullView.setPresenter(this);
        panel.setWidget(fullView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    @Override
    protected void bind() {
        super.bind();

        activityEventBus.addHandler(SearchImagery.TYPE, new SearchImageryHandler() {
            @Override
            public void onSearchImagery(SearchImagery event) {
                clientFactory.getPlaceController().goTo(new ImageSearchPlace(place.getToken()));
            }
        });
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        if(tokens.containsKey(FullViewPlace.TOKENS.companyid.toString())) {
            try {
                Long companyId = Long.parseLong(tokens.get(FullViewPlace.TOKENS.companyid.toString()));
                loadCompanyDetails(companyId);
            } catch (Exception e) {

            }
        } else if(tokens.containsKey(FullViewPlace.TOKENS.productserviceid.toString())) {
            try {
                Long productServiceId = Long.parseLong(tokens.get(FullViewPlace.TOKENS.productserviceid.toString()));
                loadProductServiceDetails(productServiceId);
            } catch (Exception e) {

            }
        } else if(tokens.containsKey(FullViewPlace.TOKENS.productid.toString())) {
            try {
                Long productId = Long.parseLong(tokens.get(FullViewPlace.TOKENS.productid.toString()));
                loadProductDetails(productId);
            } catch (Exception e) {

            }
        }
    }

    private void loadProductDetails(Long productId) {
        fullView.setTitle("Loading product details...");
        fullView.clearDetails();
        fullView.displayLoading();
        try {
            REST.withCallback(new MethodCallback<ProductDescriptionDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    fullView.hideLoading();
                    fullView.displayError("Could not find company");
                }

                @Override
                public void onSuccess(Method method, ProductDescriptionDTO productDescriptionDTO) {
                    fullView.hideLoading();
                    fullView.displayTitle("Product information");
                    fullView.displayProduct(productDescriptionDTO);
                }
            }).call(ServicesUtil.assetsService).getProductDescription(productId);
        } catch (RequestException e) {
        }
    }

    private void loadCompanyDetails(Long companyId) {
        fullView.setTitle("Loading company details...");
        fullView.clearDetails();
        fullView.displayLoading();
        try {
            REST.withCallback(new MethodCallback<CompanyDescriptionDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    fullView.hideLoading();
                    fullView.displayError("Could not find company");
                }

                @Override
                public void onSuccess(Method method, CompanyDescriptionDTO companyDescriptionDTO) {
                    fullView.hideLoading();
                    fullView.displayTitle("Company information");
                    fullView.displayCompany(companyDescriptionDTO);
                }
            }).call(ServicesUtil.assetsService).getCompanyDescription(companyId);
        } catch (RequestException e) {
        }
    }

    private void loadProductServiceDetails(Long productServiceId) {
        fullView.setTitle("Loading product service details...");
        fullView.clearDetails();
        fullView.displayLoading();
        try {
            REST.withCallback(new MethodCallback<ProductServiceDescriptionDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    fullView.hideLoading();
                    fullView.displayError("Could not find company");
                }

                @Override
                public void onSuccess(Method method, ProductServiceDescriptionDTO productServiceDescriptionDTO) {
                    fullView.hideLoading();
                    fullView.displayTitle("Product service information");
                    fullView.displayProductService(productServiceDescriptionDTO);
                }
            }).call(ServicesUtil.assetsService).getProductServiceDescription(productServiceId);
        } catch (RequestException e) {
        }
    }

}
