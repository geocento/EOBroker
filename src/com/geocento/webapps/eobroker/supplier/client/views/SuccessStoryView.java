package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.*;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface SuccessStoryView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setTitleLine(String title);

    HasText getName();

    HasClickHandlers getSubmit();

    String getImageUrl();

    void setIconUrl(String iconURL);

    HasText getDescription();

    void setDate(Date date);

    Date getDate();

    String getFullDescription();

    void setFullDescription(String fullDescription);

    List<CompanyRoleDTO> getConsortium();

    void setConsortium(List<CompanyRoleDTO> companyRoleDTOs);

    HasClickHandlers getViewClient();

    TemplateView getTemplateView();

    void setCustomer(CompanyDTO customer);

    CompanyDTO getCustomer();

    void setEndorsements(List<EndorsementDTO> endorsements);

    void setProductCategory(ProductDTO productDTO);

    ProductDTO getProductCategory();

    void setOfferings(List<ProductDatasetDTO> datasetDTOs, List<ProductServiceDTO> serviceDTOs, List<SoftwareDTO> softwareDTOs);

    public interface Presenter {
    }

}
