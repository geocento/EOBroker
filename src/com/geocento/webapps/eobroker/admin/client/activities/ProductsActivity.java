package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.ProductsPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.ProductsView;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
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
        int start = 0;
        int limit = 25;
        if(tokens.containsKey(ProductsPlace.TOKENS.start.toString())) {
            try {
                start = Integer.parseInt(tokens.get(ProductsPlace.TOKENS.start.toString()));
            } catch (Exception e) {

            }
        }
        if(tokens.containsKey(ProductsPlace.TOKENS.limit.toString())) {
            try {
                limit = Integer.parseInt(tokens.get(ProductsPlace.TOKENS.limit.toString()));
            } catch (Exception e) {

            }
        }
        orderby = tokens.get(ProductsPlace.TOKENS.orderby.toString());
        // load all products
        try {
            final int finalStart = start;
            final int finalLimit = limit;
            final String finalOrderby = orderby;
            REST.withCallback(new MethodCallback<List<ProductDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<ProductDTO> response) {
                        productsView.setProducts(finalStart, finalLimit, finalOrderby, response);
                }
            }).call(ServicesUtil.assetsService).listProducts(start, limit, orderby);
        } catch (RequestException e) {
        }
    }

    @Override
    protected void bind() {
        super.bind();
    }

}
