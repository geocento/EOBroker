package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.shared.dtos.AdminStatisticsDTO;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public interface StatsView extends IsWidget {

    HasValueChangeHandlers<String> getProductsGraphStatsType();

    HasValueChangeHandlers<String> getProductsGraphStatsDuration();

    String getProductsStatsGraphSelection();

    int getProductsStatsGraphWidthPx();

    int getProductsStatsGrapheightPx();

    String getProductsStatsGraphDateOption();

    void setProductsStatsGraphImage(String url);

    void setPresenter(Presenter presenter);

    void setStats(AdminStatisticsDTO adminStatisticsDTO);

    TemplateView getTemplateView();

    HasValueChangeHandlers<String> getUserGraphStatsType();

    HasValueChangeHandlers<String> getUserGraphStatsDuration();

    String getUserStatsGraphSelection();

    int getUserStatsGraphWidthPx();

    int getUserStatsGrapheightPx();

    String getUserStatsGraphDateOption();

    void setUserStatsGraphImage(String url);

    HasValueChangeHandlers<String> getSupplierGraphStatsType();

    HasValueChangeHandlers<String> getSupplierGraphStatsDuration();

    String getSupplierStatsGraphSelection();

    int getSupplierStatsGraphWidthPx();

    int getSupplierStatsGrapheightPx();

    String getSupplierStatsGraphDateOption();

    void setSupplierStatsGraphImage(String url);

    public interface Presenter {
    }

}
