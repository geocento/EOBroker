package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.supplier.shared.dtos.*;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;

/**
 * Created by thomas on 09/05/2016.
 */
public interface OrderView extends IsWidget {

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void displayTitle(String title);

    void displayDescription(String description);

    void displayUser(UserDTO customer);

    void setPresenter(Presenter presenter);

    TemplateView getTemplateView();

    void displayProductRequest(ProductServiceSupplierRequestDTO productServiceSupplierRequestDTO);

    void displayResponse(String supplierResponse);

    void addMessage(String imageUrl, boolean isCustomer, String message, Date date);

    void displayImageryRequest(ImageryServiceRequestDTO response);

    void displayImagesRequest(ImagesRequestDTO response);

    HasClickHandlers getSubmitMessage();

    HasClickHandlers getSubmitResponse();

    HasText getMessageText();

    String getResponse();

    void displayOTSProductRequest(OTSProductRequestDTO otsProductRequestDTO);

    public interface Presenter {
    }

}
