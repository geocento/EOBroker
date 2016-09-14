package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.supplier.shared.dtos.ImageryServiceRequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ImagesRequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceSupplierRequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.UserDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface OrderView extends IsWidget {

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void displayTitle(String title);

    void displayUser(UserDTO customer);

    void setPresenter(Presenter presenter);

    TemplateView getTemplateView();

    void displayProductRequest(ProductServiceSupplierRequestDTO productServiceSupplierRequestDTO);

    void displayImageryRequest(ImageryServiceRequestDTO response);

    void displayImagesRequest(ImagesRequestDTO response);

    public interface Presenter {
    }

}
