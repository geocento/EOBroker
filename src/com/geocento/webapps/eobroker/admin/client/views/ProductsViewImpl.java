package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.widgets.ProductsList;
import com.geocento.webapps.eobroker.admin.shared.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingWidgetList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialTextBox;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductsViewImpl extends Composite implements ProductsView {

    interface DashboardViewUiBinder extends UiBinder<Widget, ProductsViewImpl> {
    }

    private static DashboardViewUiBinder ourUiBinder = GWT.create(DashboardViewUiBinder.class);

    public static interface Style extends CssResource {
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    ProductsList products;
    @UiField
    MaterialButton createNew;
    @UiField
    MaterialTextBox filter;

    private Presenter presenter;

    public ProductsViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        products.setPresenter(new AsyncPagingWidgetList.Presenter() {
            @Override
            public void loadMore() {
                presenter.loadMore();
            }
        });

        filter.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.changeFilter(event.getValue());
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clearProducts() {
        products.clearData();
    }

    @Override
    public void addProducts(boolean hasMore, List<ProductDTO> productDTOs) {
        products.addData(productDTOs, hasMore);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public HasClickHandlers getCreateNew() {
        return createNew;
    }

    @Override
    public void setProductsLoading(boolean loading) {
        products.setLoading(loading);
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

}