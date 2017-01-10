package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.customer.shared.LayerInfoDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductDatasetVisualisationDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceVisualisationDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface VisualisationView extends IsWidget {

    void setPresenter(Presenter presenter);

    TemplateView getTemplateView();

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void displayLayerInfo(LayerInfoDTO layerInfoDTO);

    void setProductDataset(ProductDatasetVisualisationDTO productDatasetVisualisationDTO);

    void selectDataAccess(DatasetAccess datasetAccess);

    void setDataAccessDescription(String pitch);

    void addWMSLayer(LayerInfoDTO layerInfoDTO);

    void setLoadingInformation(String message);

    void hideLoadingInformation();

    void displayInformationError(String message);

    void setProductService(ProductServiceVisualisationDTO productServiceVisualisationDTO);

    public interface Presenter {
        void datasetAccessSelected(DatasetAccess datasetAccess);
    }

}
