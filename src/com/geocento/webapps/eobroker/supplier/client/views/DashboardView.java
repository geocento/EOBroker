package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.*;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface DashboardView extends IsWidget {

    void setPresenter(Presenter presenter);

    void displayServices(List<ProductServiceDTO> response);

    void displayTestimonials(List<TestimonialDTO> testimonials);

    HasClickHandlers getRequestTestimonial();

    void setCompany(CompanyDTO companyDTO);

    HasClickHandlers getAddService();

    HasClickHandlers getAddProductDataset();

    HasClickHandlers getAddDataset();

    HasClickHandlers getAddSoftware();

    HasClickHandlers getAddProject();

    HasClickHandlers editCompany();

    TemplateView getTemplateView();

    void displayDatasets(List<DatasetProviderDTO> response);

    void displayProductDatasets(List<ProductDatasetDTO> response);

    void displaySoftwares(List<SoftwareDTO> softwareDTOs);

    void displayProjects(List<ProjectDTO> projectDTOs);

    void displayMessage();

    HasClickHandlers getSaveSettings();

    SupplierSettingsDTO getSettings() throws Exception;

    void displaySettings(SupplierSettingsDTO supplierSettings);

    void displaySuccessStories(List<SuccessStoryDTO> successStoryDTOs);

    HasClickHandlers getAddSuccessStory();

    public interface Presenter {
    }

}
