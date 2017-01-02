package com.geocento.webapps.eobroker.admin.client.views;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface NewsItemView extends IsWidget {

    void setPresenter(Presenter presenter);

    HasText getNewsItemTitle();

    HasText getDescription();

    String getImageUrl();

    void setImageUrl(String imageUrl);

    HasText getWebsiteUrl();

    HasClickHandlers getSubmit();

    void setLoading(String message);

    void setLoadingError(String message);

    void hideLoading(String message);

    void setPageTitle(String title);

    TemplateView getTemplateView();

    public interface Presenter {
    }

}
