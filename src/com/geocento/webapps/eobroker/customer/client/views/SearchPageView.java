package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface SearchPageView extends IsWidget {

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void setPresenter(Presenter presenter);

    void setCurrentSearch(String search);

    void displayAoI(AoI aoi);

    void displayLoadingResults(String message);

    void hideLoadingResults();

    void clearResults();

    void setCategories(List<Category> categories);

    void setProductSelection(ProductDTO productDTO, List<ProductServiceDTO> productServices, List<ProductDTO> productDTOs);

    HasClickHandlers getChangeSearch();

    void setTitleText(String title);

    void setMatchingProducts(List<ProductDTO> suggestedProducts);

    void setMatchingServices(List<ProductServiceDTO> productServices);

    void displayProductsList(List<ProductDTO> products, int start, int limit, String text);

    void setMatchingImagery(String text);

    TemplateView getTemplateView();

    void displayCompaniesList(List<CompanyDTO> companyDTOs, int start, int limit, String text);

    public interface Presenter {
    }

}
