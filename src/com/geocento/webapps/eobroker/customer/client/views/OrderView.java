package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface OrderView extends IsWidget {

    void setPresenter(Presenter presenter);

    TemplateView getTemplateView();

    void setRequest(RequestDTO requestDTO);

    public interface Presenter {
    }

}
