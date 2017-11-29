package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.ProductCategoriesPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.ProductCategoriesView;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.ProductCategory;
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
public class ProductCategoriesActivity extends TemplateActivity implements ProductCategoriesView.Presenter {

    private ProductCategoriesView productCategoriesView;

    private int start = 0;
    private int limit = 16;
    private String orderby = "";
    private String filter;

    public ProductCategoriesActivity(ProductCategoriesPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        productCategoriesView = clientFactory.getProductCategoriesView();
        productCategoriesView.setPresenter(this);
        panel.setWidget(productCategoriesView.asWidget());
        setTemplateView(productCategoriesView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        String orderby = null;
        orderby = tokens.get(ProductCategoriesPlace.TOKENS.orderby.toString());
        // load all ProductCategories
        loadProductCategories();
    }

    private void loadProductCategories() {
        if(start == 0) {
            productCategoriesView.clearProductCategories();
        }
        try {
            productCategoriesView.setProductCategoriesLoading(true);
            REST.withCallback(new MethodCallback<List<ProductCategory>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    productCategoriesView.setProductCategoriesLoading(false);
                }

                @Override
                public void onSuccess(Method method, List<ProductCategory> response) {
                    productCategoriesView.setProductCategoriesLoading(false);
                    start += response.size();
                    productCategoriesView.addProductCategories(response.size() == limit, response);
                }
            }).call(ServicesUtil.assetsService).listProductCategories(start, limit, orderby, filter);
        } catch (RequestException e) {
        }
    }

    @Override
    protected void bind() {
        super.bind();
    }

    @Override
    public void loadMore() {
        loadProductCategories();
    }

    @Override
    public void reloadProductCategories() {
        start = 0;
        loadProductCategories();
    }

    @Override
    public void changeFilter(String value) {
        start = 0;
        filter = value;
        loadProductCategories();
    }
}
