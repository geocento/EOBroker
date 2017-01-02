package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.shared.dtos.ProductDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ProductsView extends IsWidget {

    void setPresenter(Presenter presenter);

    void clearProducts();

    void addProducts(boolean hasMore, List<ProductDTO> response);

    HasClickHandlers getCreateNew();

    void setProductsLoading(boolean loading);

    TemplateView getTemplateView();

    public interface Presenter {
        void loadMore();

        void changeFilter(String value);
    }

}
