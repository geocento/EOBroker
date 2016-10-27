package com.geocento.webapps.eobroker.supplier.client.views;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface DatasetProviderView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setTitleLine(String title);

    HasText getName();

    HasText getUri();

    HasClickHandlers getSubmit();

    String getIconUrl();

    void setIconUrl(String iconURL);

    TemplateView getTemplateView();

    void setLoading(String message);

    void setLoadingError(String message);

    void hideLoading(String message);

    public interface Presenter {
    }

}
