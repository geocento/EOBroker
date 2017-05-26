package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveProductDataset;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveProductDatasetHandler;
import com.geocento.webapps.eobroker.supplier.client.places.ProductDatasetPlace;
import com.geocento.webapps.eobroker.supplier.client.places.ProductDatasetsPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.DashboardView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.OfferDTO;
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
public class ProductDatasetsActivity extends TemplateActivity implements DashboardView.Presenter {

    private DashboardView dashboardView;

    public ProductDatasetsActivity(ProductDatasetsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        dashboardView = clientFactory.getDashboardView();
        dashboardView.setPresenter(this);
        setTemplateView(dashboardView.getTemplateView());
        panel.setWidget(dashboardView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        loadProductDatasets();
    }

    private void loadProductDatasets() {
        try {
            displayFullLoading("Loading off the shelf products...");
            REST.withCallback(new MethodCallback<OfferDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideFullLoading();
                    Window.alert("Error loading assets...");
                }

                @Override
                public void onSuccess(Method method, OfferDTO response) {
                    hideFullLoading();
                    dashboardView.displayProductDatasets(response.getProductDatasetDTOs());
                }

            }).call(ServicesUtil.assetsService).getOffer(Category.productdatasets);
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void bind() {
        super.bind();

        activityEventBus.addHandler(RemoveProductDataset.TYPE, new RemoveProductDatasetHandler() {
            @Override
            public void onRemoveProductDataset(RemoveProductDataset event) {
                if (Window.confirm("Are you sure you want to remove this off the shelf product?")) {
                    try {
                        REST.withCallback(new MethodCallback<Void>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                displayError("Could not remove this off the shelf product");
                            }

                            @Override
                            public void onSuccess(Method method, Void response) {
                                displaySuccess("Off the shelf product has been removed");
                                // reload datasets
                                loadProductDatasets();
                            }
                        }).call(ServicesUtil.assetsService).removeProductDataset(event.getId());
                    } catch (Exception e) {

                    }
                }
            }
        });

        handlers.add(dashboardView.getAddProductDataset().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new ProductDatasetPlace());
            }
        }));

    }

}
