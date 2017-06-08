package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.shared.*;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface FullView extends IsWidget {

    void setPresenter(Presenter presenter);

    void displayCompany(CompanyDescriptionDTO companyDescriptionDTO);

    void setTitle(String title);

    void clearDetails();

    void displayProductService(ProductServiceDescriptionDTO productServiceDescriptionDTO);

    void displayProduct(ProductDescriptionDTO productDescriptionDTO);

    void displayProductDataset(ProductDatasetDescriptionDTO productDatasetDescriptionDTO);

    void displaySoftware(SoftwareDescriptionDTO softwareDescriptionDTO);

    void displayProject(ProjectDescriptionDTO projectDescriptionDTO);

    void selectSection(String tabName);

    public interface Presenter {
    }

}
