package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.shared.dtos.DatasetProviderDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface DatasetProvidersView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setDatasets(List<DatasetProviderDTO> response);

    HasClickHandlers getCreateNewButton();

    TemplateView getTemplateView();

    public interface Presenter {
    }

}
