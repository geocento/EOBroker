package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
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
    }

    @Override
    public void loadMore() {
        loadProducts();
    }

    @Override
    public void changeFilter(String value) {
        start = 0;
        filter = value;
        loadProducts();
    }
}
