package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceSupplierResponseDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ProductResponseView extends IsWidget {

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void displayTitle(String title);

    void displayImage(String imageURL);

    void displayComment(String comment);

    void displayResponseSupplier(String supplierIconUrl, String supplierName);

    void addMessage(String imageUrl, boolean isCustomer, String message, Date date);

    HasClickHandlers getSubmitMessage();

    HasText getMessageText();

    void setPresenter(Presenter presenter);

    void displayProductRequest(ProductServiceResponseDTO productServiceResponseDTO);

    void displayProductResponse(ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO);

    public interface Presenter extends RequestView.Presenter {
        void responseSelected(ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO);
    }

}
