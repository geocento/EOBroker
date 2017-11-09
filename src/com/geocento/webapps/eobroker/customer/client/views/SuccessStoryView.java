package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.shared.SuccessStoryDTO;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface SuccessStoryView extends IsWidget {

    void setPresenter(Presenter presenter);

    void displaySuccessStory(SuccessStoryDTO successStoryDTO);

    public interface Presenter {
    }

}
