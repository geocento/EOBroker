package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.ApplicationSettings;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface SettingsView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setSettings(ApplicationSettings settings);

    ApplicationSettings getSettings() throws Exception;

    HasClickHandlers getSubmit();

    TemplateView getTemplateView();

    public interface Presenter {
    }

}
