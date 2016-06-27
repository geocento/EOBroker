package com.geocento.webapps.eobroker.admin.client.views;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface DashboardView extends IsWidget {

    void setPresenter(Presenter presenter);

    public interface Presenter {
    }

}
