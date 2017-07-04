package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.admin.shared.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingWidgetList;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by thomas on 21/06/2016.
 */
public class ProductsList extends AsyncPagingWidgetList<ProductDTO> {

    private int pagerSize;
    private String orderBy;

    public ProductsList() {
        this(10);
    }

    public ProductsList(int pageSize) {
        super(pageSize, 12, 4, 3);
    }

    @Override
    protected Widget getItemWidget(ProductDTO value) {
        return new ProductWidget(value);
    }

}
