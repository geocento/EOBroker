package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.StatisticsPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.StatisticsView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierStatisticsDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class StatisticsActivity extends TemplateActivity implements StatisticsView.Presenter {

    private StatisticsView statisticsView;

    public StatisticsActivity(StatisticsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        statisticsView = clientFactory.getStatisticsView();
        statisticsView.setPresenter(this);
        setTemplateView(statisticsView.getTemplateView());
        panel.setWidget(statisticsView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        try {
            displayFullLoading("Loading statistics...");
            REST.withCallback(new MethodCallback<SupplierStatisticsDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideFullLoading();
                    Window.alert("Error loading statistics...");
                }

                @Override
                public void onSuccess(Method method, SupplierStatisticsDTO response) {
                    hideFullLoading();
                    statisticsView.displayStatistics(response);
                    updateProductsViewStats();
                    updateViewStats();
                    updateSearchStats();
                }

            }).call(ServicesUtil.assetsService).getStatistics();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(statisticsView.getViewStatsOptions().addValueChangeHandler((ValueChangeHandler) event -> updateViewStats()));
        handlers.add(statisticsView.getViewStatsDateOptions().addValueChangeHandler((ValueChangeHandler) event -> updateViewStats()));

        handlers.add(statisticsView.getSearchStatsOptions().addValueChangeHandler((ValueChangeHandler) event -> updateSearchStats()));
        handlers.add(statisticsView.getSearchStatsDateOptions().addValueChangeHandler((ValueChangeHandler) event -> updateSearchStats()));

        handlers.add(statisticsView.getProductsStatsOptions().addValueChangeHandler((ValueChangeHandler) event -> updateProductsViewStats()));
        handlers.add(statisticsView.getProductsStatsDateOptions().addValueChangeHandler((ValueChangeHandler) event -> updateProductsViewStats()));
    }

    private void updateViewStats() {
        statisticsView.setViewStatsImage(GWT.getModuleBaseURL() + "api/stats/view/?" +
                        "ids=" + StringUtils.join(statisticsView.getSelectedViewStatsOptions(), ",") +
                        "&width=" + statisticsView.getViewStatsWidthPx() +
                        "&height=" + statisticsView.getViewStatsHeightPx() +
                        "&dateOption=" + statisticsView.getViewStatsDateOption()
        );
    }

    private void updateSearchStats() {
        statisticsView.setSearchStatsImage(GWT.getModuleBaseURL() + "api/stats/search/?" +
                        "ids=" + StringUtils.join(statisticsView.getSelectedSearchStatsOptions(), ",") +
                        "&width=" + statisticsView.getSearchStatsWidthPx() +
                        "&height=" + statisticsView.getSearchStatsHeightPx() +
                        "&dateOption=" + statisticsView.getSearchStatsDateOption()
        );
    }

    private void updateProductsViewStats() {
        statisticsView.setProductsStatsImage(GWT.getModuleBaseURL() + "api/stats/productsview/?" +
                        "ids=" + StringUtils.join(statisticsView.getSelectedProductsStatsOptions(), ",") +
                        "&width=" + statisticsView.getProductsStatsWidthPx() +
                        "&height=" + statisticsView.getProductsStatsHeightPx() +
                        "&dateOption=" + statisticsView.getProductsStatsDateOption()
        );
    }

}
