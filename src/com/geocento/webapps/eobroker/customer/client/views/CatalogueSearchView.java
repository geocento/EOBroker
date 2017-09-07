package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.opensearch.Record;
import com.geocento.webapps.eobroker.common.client.utils.opensearch.SearchResponse;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.customer.shared.ProductDatasetCatalogueDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface CatalogueSearchView extends IsWidget {

    void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler);

    void setPresenter(Presenter presenter);

    void displayAoI(AoIDTO aoi);

    HasText getQuery();

    void displayLoadingResults(String message);

    void hideLoadingResults();

    void displayQueryResponse(SearchResponse records, boolean isOrderable);

    void displayStartDate(Date date);

    void displayStopDate(Date date);

    HasClickHandlers getUpdateButton();

    void enableUpdate(boolean enable);

    void clearMap();

    HasClickHandlers getQuoteButton();

    HasClickHandlers getSubmitRequestButton();

    HasText getRequestName();

    HasText getRequestComment();

    void displayRequestForQuotation(String title, String description);

    void hideRequestQuotation();

    List<Record> getSelectedRecord();

    void clearRecordsSelection();

    void showQuery();

    void centerOnAoI();

    void setProductDatasetCatalogDTO(ProductDatasetCatalogueDTO productDatasetCatalogueDTO);

    void setParameters(List<FormElement> formElements);

    List<com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue> getExtraParameters() throws Exception;

    public interface Presenter {

        void aoiChanged(AoIDTO aoi);

        void onStartDateChanged(Date value);

        void onStopDateChanged(Date value);

        void onQueryChanged(String value);

        void onRecordRangeChanged(int start, int length);
    }

}
