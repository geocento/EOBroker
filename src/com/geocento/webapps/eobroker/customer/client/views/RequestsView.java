package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.requests.RequestDTO;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface RequestsView extends IsWidget {

    void setPresenter(Presenter presenter);

    TemplateView getTemplateView();

    void setRequests(List<RequestDTO> requestDTOs);

    public interface Presenter {
    }

}
