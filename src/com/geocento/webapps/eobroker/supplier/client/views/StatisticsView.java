package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierStatisticsDTO;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface StatisticsView extends IsWidget {

    void setPresenter(Presenter presenter);

    TemplateView getTemplateView();

    void displayStatistics(SupplierStatisticsDTO response);

    HasChangeHandlers getViewStatsOptions();

    java.util.List<String> getSelectedViewStatsOptions();

    void setViewStatsImage(String imageUrl);

    void displayViewStatsLoading(String message);

    int getViewStatsWidthPx();

    int getViewStatsHeightPx();

    String getViewStatsDateOption();

    HasChangeHandlers getViewStatsDateOptions();

    public interface Presenter {
    }

}
