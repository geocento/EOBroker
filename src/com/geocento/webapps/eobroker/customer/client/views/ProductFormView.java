package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.customer.shared.ProductFormDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceFormDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ProductFormView extends IsWidget {

    void displayAoI(AoIDTO aoi);

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void setPresenter(Presenter presenter);

    void setProduct(ProductFormDTO productFormDTO);

    void setProductService(ProductServiceFormDTO productServiceFormDTO);

    HasClickHandlers getSubmit();

    java.util.List<com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue> getFormElementValues() throws Exception;

    void displayFormValidationError(String message);

    List<ProductServiceDTO> getSelectedServices();

    void clearRequest();

    void setFormElementValues(List<FormElementValue> values);

    public interface Presenter {
    }

}
