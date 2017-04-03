package com.geocento.webapps.eobroker.customer.client.views;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface SettingsView extends IsWidget {

    TemplateView getTemplateView();

    void setPresenter(Presenter presenter);

    public interface Presenter {
    }

}
