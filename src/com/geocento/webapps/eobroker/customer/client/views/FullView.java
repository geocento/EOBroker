package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.shared.CompanyDescriptionDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductDescriptionDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceDescriptionDTO;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface FullView extends IsWidget {

    void setPresenter(Presenter presenter);

    void hideLoading();

    void displayError(String message);

    void displaySuccess(String message);

    void displayLoading();

    void displayCompany(CompanyDescriptionDTO companyDescriptionDTO);

    void setTitle(String title);

    void clearDetails();

    void displayTitle(String title);

    void displayProductService(ProductServiceDescriptionDTO productServiceDescriptionDTO);

    void displayProduct(ProductDescriptionDTO productDescriptionDTO);

    TemplateView getTemplateView();

    public interface Presenter {
    }

}
