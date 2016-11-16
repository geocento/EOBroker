package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;
import com.geocento.webapps.eobroker.common.shared.entities.ServiceType;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ProductDatasetView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setTitleLine(String title);

    HasText getName();

    void setServiceType(ServiceType serviceType);

    ServiceType getServiceType();

    HasClickHandlers getSubmit();

    String getImageUrl();

    void setIconUrl(String iconURL);

    HasText getDescription();

    String getFullDescription();

    void setFullDescription(String fullDescription);

    ProductDTO getSelectProduct();

    void setSelectedProduct(ProductDTO productDTO);

    TemplateView getTemplateView();

    void setExtent(AoIDTO aoi);

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    AoIDTO getExtent();

    void setDataAccess(List<DatasetAccess> datasetAccesses);

    List<DatasetAccess> getDataAccesses();

    void setSampleDataAccess(List<DatasetAccess> samples);

    void setFeatures(List<FeatureDescription> features);

    List<DatasetAccess> getSamples();

    List<FeatureDescription> getFeatures();

    public interface Presenter {
    }

}
