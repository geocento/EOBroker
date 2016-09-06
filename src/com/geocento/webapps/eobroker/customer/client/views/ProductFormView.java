package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ProductFormView extends IsWidget {

    void setPresenter(Presenter presenter);

    void clearForm();

    void addFormElement(FormElement formElement);

    void clearSuppliers();

    void addProductService(ProductServiceDTO productServiceDTO);

    void setProductImage(String imageUrl);

    void setProductName(String name);

    void setProductDescription(String description);

    HasClickHandlers getSubmit();

    void setInformationUrl(String url);

    void displayLoading(String message);

    void hideLoading();

    void displayError(String message);

    void displaySuccess(String message);

    void displayLoading();

    java.util.List<com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue> getFormElementValues() throws Exception;

    void displayFormValidationError(String message);

    List<ProductServiceDTO> getSelectedServices();

    void displaySubmittedSuccess(String message);

    TemplateView getTemplateView();

    public interface Presenter {
    }

}
