package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingWidgetList;
import com.geocento.webapps.eobroker.common.shared.entities.ProductCategory;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by thomas on 21/06/2016.
 */
public class ProductCategoriesList extends AsyncPagingWidgetList<ProductCategory> {

    private int pagerSize;
    private String orderBy;

    public ProductCategoriesList() {
        this(10);
    }

    public ProductCategoriesList(int pageSize) {
        super(pageSize, 12, 4, 3);
    }

    @Override
    protected Widget getItemWidget(ProductCategory value) {
        return new ProductCategoryWidget(value);
    }

}
