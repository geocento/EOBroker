package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.events.RemoveProduct;
import com.geocento.webapps.eobroker.admin.client.events.RemoveProductHandler;
import com.geocento.webapps.eobroker.admin.client.places.ProductPlace;
import com.geocento.webapps.eobroker.admin.client.places.ProductsPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.ProductsView;
import com.geocento.webapps.eobroker.admin.shared.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
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
public class ProductsActivity extends TemplateActivity implements ProductsView.Presenter {

    private ProductsView productsView;

    private int start = 0;
    private int limit = 16;
    private String orderby = "";
    private String filter;

    public ProductsActivity(ProductsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        productsView = clientFactory.getProductsView();
        productsView.setPresenter(this);
        panel.setWidget(productsView.asWidget());
        setTemplateView(productsView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        String orderby = null;
        orderby = tokens.get(ProductsPlace.TOKENS.orderby.toString());
        // load all products
        loadProducts();
    }

    private void loadProducts() {
        if(start == 0) {
            productsView.clearProducts();
        }
        try {
            productsView.setProductsLoading(true);
            REST.withCallback(new MethodCallback<List<ProductDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    productsView.setProductsLoading(false);
                }

                @Override
                public void onSuccess(Method method, List<ProductDTO> response) {
                    productsView.setProductsLoading(false);
                    start += response.size();
                    productsView.addProducts(response.size() == limit, response);
                }
            }).call(ServicesUtil.assetsService).listProducts(start, limit, orderby, filter);
        } catch (RequestException e) {
        }
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(productsView.getCreateNew().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new ProductPlace());
            }
        }));

        activityEventBus.addHandler(RemoveProduct.TYPE, new RemoveProductHandler() {
            @Override
            public void onRemoveProduct(RemoveProduct event) {
                if(!Window.confirm("Are you sure you want to remove this product?")) {
                    return;
                }
                try {
                    displayLoading("Removing product...");
                    REST.withCallback(new MethodCallback<Void>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError("Problem removing product, reason is " + method.getResponse().getText());
                        }

                        @Override
                        public void onSuccess(Method method, Void response) {
                            hideLoading();
                            displaySuccess("Product has been removed");
                            reloadProducts();
                        }
                    }).call(ServicesUtil.assetsService).removeProduct(event.getId());
                } catch (RequestException e) {
                }
            }
        });
    }

    @Override
    public void loadMore() {
        loadProducts();
    }

    private void reloadProducts() {
        start = 0;
        loadProducts();
    }

    @Override
    public void changeFilter(String value) {
        start = 0;
        filter = value;
        loadProducts();
    }
}
