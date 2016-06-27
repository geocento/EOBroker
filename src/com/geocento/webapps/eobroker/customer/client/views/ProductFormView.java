package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ProductFormView extends IsWidget {

    void setPresenter(Presenter presenter);

    void clearForm();

    void addFormElement(FormElement formElement);

    void clearSuppliers();

    void addSupplier(ProductServiceDTO productServiceDTO);

    void setProductImage(String imageUrl);

    void setProductName(String name);

    void setProductDescription(String description);

    void displayLoading(String message);

    void hideLoading();

    void displayError(String message);

    void displaySuccess(String message);

    void displayLoading();

    public interface Presenter {
    }

}
