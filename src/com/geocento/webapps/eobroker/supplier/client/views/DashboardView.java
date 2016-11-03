package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.DatasetProviderDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDatasetDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface DashboardView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setServices(List<ProductServiceDTO> response);

    void setCompany(CompanyDTO companyDTO);

    HasClickHandlers getAddService();

    HasClickHandlers getAddProductDataset();

    HasClickHandlers getAddDataset();

    HasClickHandlers editCompany();

    TemplateView getTemplateView();

    void setDatasets(List<DatasetProviderDTO> response);

    void setProductDatasets(List<ProductDatasetDTO> response);

    public interface Presenter {
    }

}
