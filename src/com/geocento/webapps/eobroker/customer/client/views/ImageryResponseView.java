package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.shared.requests.ImageryResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ImagerySupplierResponseDTO;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ImageryResponseView extends RequestView {

    void setPresenter(Presenter presenter);

    void displayImageryRequest(ImageryResponseDTO productServiceResponseDTO);

    void displayImageryResponse(ImagerySupplierResponseDTO imagerySupplierResponseDTO);

    public interface Presenter extends RequestView.Presenter {
        void responseSelected(ImagerySupplierResponseDTO imagerySupplierResponseDTO);
    }

}
