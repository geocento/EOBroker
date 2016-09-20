package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceSupplierResponseDTO;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ProductResponseView extends RequestView {

    void setPresenter(Presenter presenter);

    void displayProductRequest(ProductServiceResponseDTO productServiceResponseDTO);

    void displayProductResponse(ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO);

    public interface Presenter extends RequestView.Presenter {
        void responseSelected(ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO);
    }

}
