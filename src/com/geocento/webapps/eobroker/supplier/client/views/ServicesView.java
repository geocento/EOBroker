package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ServicesView extends IsWidget {

    void setPresenter(Presenter presenter);

    void setTitleLine(String title);

    HasText getName();

    HasText getEmail();

    HasText getWebsite();

    HasText getDescription();

    String getFullDescription();

    void setFullDescription(String fullDescription);

    HasClickHandlers getSubmit();

    String getIconUrl();

    void setIconUrl(String iconURL);

    ProductDTO getSelectProduct();

    void setSelectedProduct(ProductDTO productDTO);

    HasText getAPIUrl();

    HasText getSampleWmsUrl();

    void displayLoading(String message);

    void hideLoading();

    void displayError(String message);

    void displaySuccess(String message);

    TemplateView getTemplateView();

    public interface Presenter {
    }

}
