package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.REGISTRATION_STATUS;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface CompanyView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setTitleLine(String title);

    HasText getName();

    HasValue<Boolean> getSupplier();

    HasValue<REGISTRATION_STATUS> getStatus();

    HasText getEmail();

    void displayValidate(boolean display);

    HasClickHandlers getValidate();

    HasText getWebsite();

    HasText getDescription();

    String getFullDescription();

    void setFullDescription(String fullDescription);

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
