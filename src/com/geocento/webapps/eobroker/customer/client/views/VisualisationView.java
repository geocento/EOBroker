package com.geocento.webapps.eobroker.customer.client.views;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface VisualisationView extends IsWidget {

    void setPresenter(Presenter presenter);

    TemplateView getTemplateView();

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void addWMSLayer(String wmsUrl, String layerName);

    public interface Presenter {
    }

}
