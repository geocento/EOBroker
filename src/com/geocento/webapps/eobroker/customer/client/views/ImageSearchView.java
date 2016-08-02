package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.ImageryService;
import com.geocento.webapps.eobroker.common.shared.imageapi.Product;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ImageSearchView extends IsWidget {

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void setPresenter(Presenter presenter);

    void displayAoI(AoI aoi);

    void setText(String text);

    void setSuppliers(List<ImageryService> imageryServices);

    void displaySupplier(ImageryService imageryService);

    void displayLoadingResults(String message);

    void hideLoadingResults();

    void displayImageProducts(List<Product> imageProductDTOs);

    void displayStartDate(Date date);

    void displayStopDate(Date date);

    void displaySensors(String sensors);

    HasClickHandlers getUpdateButton();

    void enableUpdate(boolean enable);

    void clearMap();

    public interface Presenter {

        void aoiChanged(AoI aoi);

        void onProviderChanged(ImageryService imageryService);

        void onStartDateChanged(Date value);

        void onStopDateChanged(Date value);

        void onSensorsChanged(String value);
    }

}
