package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.ProductCategory;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ProductCategoriesView extends IsWidget {

    void setPresenter(Presenter presenter);

    void clearProductCategories();

    void addProductCategories(boolean hasMore, List<ProductCategory> response);

    HasClickHandlers getCreateNew();

    void setProductCategoriesLoading(boolean loading);

    TemplateView getTemplateView();

    public interface Presenter {
        void loadMore();

        void changeFilter(String value);

        void reloadProductCategories();
    }

}
