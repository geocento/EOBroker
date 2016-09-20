package com.geocento.webapps.eobroker.customer.client.views;

import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;

/**
 * Created by thomas on 09/05/2016.
 */
public interface RequestView extends IsWidget {

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void displayTitle(String title);

    void displayComment(String comment);

    TemplateView getTemplateView();

    void addMessage(String imageUrl, boolean isCustomer, String message, Date date);

    HasClickHandlers getSubmitMessage();

    HasText getMessageText();

    public interface Presenter {
    }

}
