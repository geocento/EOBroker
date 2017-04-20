package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface TestimonialView extends IsWidget {

    HasClickHandlers getCreateButton();

    HasHTML getTestimonial();

    HasValue<Category> getCategory();

    void setPresenter(Presenter presenter);

    HasText getSearchText();

    public interface Presenter {
        void selectSuggestion(Suggestion suggestion);
    }

}
