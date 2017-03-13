package com.geocento.webapps.eobroker.admin.client.views;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface LogsView extends IsWidget {

    TemplateView getTemplateView();

    void setPresenter(Presenter presenter);

    void setLogs(String response);

    HasClickHandlers getReload();

    public interface Presenter {
    }

}
