package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.places.ProductsPlace;
import com.geocento.webapps.eobroker.admin.client.widgets.ProductsList;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingCellTable;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

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

    private Presenter presenter;

    public ProductsViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        products.setPresenter(new AsyncPagingCellTable.Presenter() {
            @Override
            public void rangeChanged(int start, int length, Column<?, ?> column, boolean isAscending) {
                clientFactory.getPlaceController().goTo(new ProductsPlace(start, length, null));
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setProducts(int start, int limit, String orderby, List<ProductDTO> productDTOs) {
        this.products.setPagerSize(limit);
        this.products.setOrderBy(orderby);
        this.products.setRowData(start, productDTOs);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}