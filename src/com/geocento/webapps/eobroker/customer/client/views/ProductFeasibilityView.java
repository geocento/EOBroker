package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceFeasibilityDTO;
import com.geocento.webapps.eobroker.customer.shared.feasibility.ProductFeasibilityResponse;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import gwt.material.design.client.base.HasHref;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface ProductFeasibilityView extends IsWidget {

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void setPresenter(Presenter presenter);

    void displayAoI(AoIDTO aoi);

    void centerOnAoI();

    void setText(String text);

    void displayLoadingResults(String message);

    void hideLoadingResults();

    HasClickHandlers getUpdateButton();

    void enableUpdate(boolean enable);

    void clearMap();

    void displayLoading(String message);

    void setServices(List<ProductServiceFeasibilityDTO> productServices);

    void setFormElements(List<FormElement> apiFormElements);

    void hideLoading();

    void displayError(String message);

    void selectService(ProductServiceFeasibilityDTO value);

    void setStart(Date start);

    void setStop(Date stop);

    void setFormElementValues(List<FormElementValue> formElementValues);

    void displayResultsError(String message);

    void displayResponse(ProductFeasibilityResponse response);

    void clearResults();

    TemplateView getTemplateView();

    void showQuery();

    HasClickHandlers getRequestButton();

    HasClickHandlers getContactButton();

    public interface Presenter {

        void aoiChanged(AoIDTO aoi);

        void onServiceChanged(ProductServiceFeasibilityDTO imageryService);

        void onStartDateChanged(Date value);

        void onStopDateChanged(Date value);

        void onFormElementChanged(FormElement formElement);
    }

}
