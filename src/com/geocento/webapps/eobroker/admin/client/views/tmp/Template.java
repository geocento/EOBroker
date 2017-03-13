package com.geocento.webapps.eobroker.admin.client.views.tmp;

import com.geocento.webapps.eobroker.admin.client.views.TemplateView;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface Template extends IsWidget {

    TemplateView getTemplateView();

    void setPresenter(Presenter presenter);

    public interface Presenter {
    }

}
