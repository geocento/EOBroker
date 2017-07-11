package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.PointJSNI;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.customer.shared.LayerInfoDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductDatasetVisualisationDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceVisualisationDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface VisualisationView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void setQueryable(boolean queryable);

    HasValue<Boolean> getFeatureInfoSwitch();

    void displayLayerInfo(LayerInfoDTO layerInfoDTO);

    void setProductDataset(ProductDatasetVisualisationDTO productDatasetVisualisationDTO);

    void setAdditionalDatasets(List<DatasetAccess> datasetAccesses);

    void selectDataAccess(DatasetAccess datasetAccess);

    void setDataAccessDescription(String pitch);

    void setWMSLayer(LayerInfoDTO layerInfoDTO);

    void setLoadingInformation(String message);

    void hideLoadingInformation();

    void displayInformationError(String message);

    void setProductService(ProductServiceVisualisationDTO productServiceVisualisationDTO);

    void enableGetFeatureInfo(boolean queryable);

    void displayMapInfoLoading(PointJSNI location, String title, String message);

    void hideMapInfoLoading(PointJSNI location, String message);

    void displayMapInfoContent(PointJSNI location, String title, String content);

    void displayGetFeatureInfoContent(String content);

    void displayGetFeatureInfoLoading(String message);

    void enableWCS(boolean enable);

    HasClickHandlers getDownloadSample();

    void displayDownload(boolean display);

    void setDownloadTooltip(String message);

    public interface Presenter {
        void datasetAccessSelected(DatasetAccess datasetAccess);
    }

}
