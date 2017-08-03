package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.*;
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
public interface ProductServiceView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setTitleLine(String title);

    HasText getName();

    HasText getEmail();

    HasText getWebsite();

    HasText getDescription();

    String getFullDescription();

    void setFullDescription(String fullDescription);

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    HasClickHandlers getSubmit();

    String getIconUrl();

    void setIconUrl(String iconURL);

    ProductDTO getSelectedProduct();

    void setSelectedProduct(ProductDTO productDTO);

    List<FeatureDescription> getSelectedGeoinformation();

    void setProductGeoinformation(List<FeatureDescription> featureDescriptions);

    void setSelectedGeoinformation(List<FeatureDescription> featureDescriptions);

    HasText getGeoinformationComment();

    List<PerformanceValue> getSelectedPerformances();

    void setProductPerformances(List<PerformanceDescription> performanceDescriptions);

    void setProvidedPerformances(List<PerformanceValue> performanceValues);

    HasText getPerformancesComment();

    HasText getDeliveryTime();

    HasText getDisseminationComment();

    void setExtent(AoIDTO extent);

    AoIDTO getExtent();

    void setCoverageLayers(List<DatasetAccessOGC> coverageLayers);

    List<DatasetAccessOGC> getCoverageLayers();

    HasText getAPIUrl();

    TemplateView getTemplateView();

    void setSelectedDataAccessTypes(List<AccessType> selectedAccessTypes);

    List<AccessType> getSelectedDataAccessTypes();

    void setSampleProductServiceId(Long datasetId);

    void setSampleDataAccess(List<DatasetAccess> samples);

    List<DatasetAccess> getSamples();

    void setTermsAndConditions(String termsAndConditions);

    String getTermsAndConditions();

    HasClickHandlers getViewClient();

    public interface Presenter {

        void productChanged();

        void viewDataAccess(DatasetAccess datasetAccess);
    }

}
