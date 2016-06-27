package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ProductsView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setProducts(int finalStart, int finalLimit, String finalOrderby, List<ProductDTO> response);

    public interface Presenter {
    }

}
