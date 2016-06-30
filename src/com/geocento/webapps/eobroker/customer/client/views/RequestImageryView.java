package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface RequestImageryView extends IsWidget {

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void displayAoI(AoI aoi);

    void setPresenter(Presenter presenter);

    void displaySearchError(String message);

    void setDescription(String description);

    public interface Presenter {

        void aoiChanged(AoI aoi);
    }

}
