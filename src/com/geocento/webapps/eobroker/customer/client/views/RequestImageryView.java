package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.ImageService;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface RequestImageryView extends IsWidget {

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void displayAoI(AoIDTO aoi);

    void setPresenter(Presenter presenter);

    void displaySearchError(String message);

    void setDescription(String description);

    void setSuppliers(List<ImageService> imageServices);

    HasClickHandlers getSubmitButton();

    String getImageType();

    Date getStartDate();

    Date getStopDate();

    String getAdditionalInformation();

    List<ImageService> getSelectedServices();

    void displaySubmitLoading(boolean display);

    String getApplication();

    void displayFormError(String message);

    void clearRequest();

    public interface Presenter {
        void aoiChanged(AoIDTO aoi);
    }

}
