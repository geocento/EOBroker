package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierStatisticsDTO;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface StatisticsView extends IsWidget {

    void setPresenter(Presenter presenter);

    TemplateView getTemplateView();

    void displayStatistics(SupplierStatisticsDTO response);

    HasValueChangeHandlers<String> getViewStatsOptions();

    List<String> getSelectedViewStatsOptions();

    void setViewStatsImage(String imageUrl);

    void displayViewStatsLoading(String message);

    int getViewStatsWidthPx();

    int getViewStatsHeightPx();

    String getViewStatsDateOption();

    HasValueChangeHandlers<String> getViewStatsDateOptions();

    HasValueChangeHandlers<String> getSearchStatsOptions();

    HasValueChangeHandlers<String> getSearchStatsDateOptions();

    List<String> getSelectedSearchStatsOptions();

    void setSearchStatsImage(String imageUrl);

    void displaySearchStatsLoading(String message);

    int getSearchStatsWidthPx();

    int getSearchStatsHeightPx();

    String getSearchStatsDateOption();

    HasValueChangeHandlers<String> getProductsStatsOptions();

    List<String> getSelectedProductsStatsOptions();

    void setProductsStatsImage(String imageUrl);

    void displayProductsStatsLoading(String message);

    int getProductsStatsWidthPx();

    int getProductsStatsHeightPx();

    String getProductsStatsDateOption();

    HasValueChangeHandlers<String> getProductsStatsDateOptions();

    public interface Presenter {
    }

}
