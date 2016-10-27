package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.ImageService;
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

    void displayAoI(AoI aoi);

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

    TemplateView getTemplateView();

    void displaySubmitLoading(boolean display);

    void displaySucces(String message);

    String getApplication();

    public interface Presenter {
        void aoiChanged(AoI aoi);
    }

}
