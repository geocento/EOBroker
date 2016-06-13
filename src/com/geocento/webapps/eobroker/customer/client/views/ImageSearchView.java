package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.imageapi.ImageProductDTO;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ImageSearchView extends IsWidget {

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void setPresenter(Presenter presenter);

    void displaySupplier(String name, String imageUrl);

    void displayAoI(AoI aoi);

    void setText(String text);

    void displayLoadingResults(String message);

    void hideLoadingResults();

    void displayImageProducts(List<ImageProductDTO> imageProductDTOs);

    public interface Presenter {
        void aoiChanged(AoI aoi);
    }

}
