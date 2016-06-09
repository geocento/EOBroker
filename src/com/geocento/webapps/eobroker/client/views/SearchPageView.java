package com.geocento.webapps.eobroker.client.views;

import com.geocento.webapps.eobroker.shared.entities.Category;
import com.geocento.webapps.eobroker.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.shared.entities.dtos.ProductServiceDTO;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface SearchPageView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setCurrentSearch(String search);

    void displayLoadingResults(String message);

    void hideLoadingResults();

    void clearResults();

    void addProduct(ProductDTO productDTO, List<ProductServiceDTO> services);

    void setCategory(Category category);

    public interface Presenter {
    }

}
