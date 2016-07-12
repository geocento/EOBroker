package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.supplier.shared.dtos.UserDTO;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface OrderView extends IsWidget {

    void displayTitle(String title);

    void displayUser(UserDTO customer);

    void displayFormValue(List<FormElementValue> formValues);

    void setPresenter(Presenter presenter);

    TemplateView getTemplateView();

    public interface Presenter {
    }

}
