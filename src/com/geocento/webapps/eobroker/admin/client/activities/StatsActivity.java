package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.StatsPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.StatsView;
import com.geocento.webapps.eobroker.admin.shared.dtos.AdminStatisticsDTO;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
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
public class StatsActivity extends TemplateActivity implements StatsView.Presenter {

    private StatsView statsView;

    public StatsActivity(StatsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        statsView = clientFactory.getStatsView();
        statsView.setPresenter(this);
        panel.setWidget(statsView.asWidget());
        setTemplateView(statsView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        try {
            displayLoading("Loading statistics...");
            REST.withCallback(new MethodCallback<AdminStatisticsDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideLoading();
                    Window.alert("Error loading statistics...");
                }

                @Override
                public void onSuccess(Method method, AdminStatisticsDTO response) {
                    hideLoading();
                    statsView.setStats(response);
                    updateUserGraphStats();
                    updateSupplierGraphStats();
                    updateProductsGraphStats();
                    updatePlatformGraphStats();
                }

            }).call(ServicesUtil.assetsService).getAdminStatistics();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(statsView.getUserGraphStatsType().addValueChangeHandler((ValueChangeHandler) event -> updateUserGraphStats()));
        handlers.add(statsView.getUserGraphStatsDuration().addValueChangeHandler((ValueChangeHandler) event -> updateUserGraphStats()));

        handlers.add(statsView.getSupplierGraphStatsType().addValueChangeHandler((ValueChangeHandler) event -> updateSupplierGraphStats()));
        handlers.add(statsView.getSupplierGraphStatsDuration().addValueChangeHandler((ValueChangeHandler) event -> updateSupplierGraphStats()));

        handlers.add(statsView.getProductsGraphStatsType().addValueChangeHandler((ValueChangeHandler) event -> updateProductsGraphStats()));
        handlers.add(statsView.getProductsGraphStatsDuration().addValueChangeHandler((ValueChangeHandler) event -> updateProductsGraphStats()));

        handlers.add(statsView.getPlatformGraphStatsType().addValueChangeHandler((ValueChangeHandler) event -> updatePlatformGraphStats()));
        handlers.add(statsView.getPlatformGraphStatsDuration().addValueChangeHandler((ValueChangeHandler) event -> updatePlatformGraphStats()));

        handlers.add(statsView.getPlatformReindex().addClickHandler(event -> reindexSearches()));

    }

    private void reindexSearches() {
        displayLoading("Reindexing searches...");
        try {
            REST.withCallback(new MethodCallback<Void>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideLoading();
                    displayError(method.getResponse().getText());
                }

                @Override
                public void onSuccess(Method method, Void response) {
                    hideLoading();
                    displaySuccess("Reindexing successful");
                }
            }).call(ServicesUtil.assetsService).reindexSearches();
        } catch (Exception e) {

        }
    }

    private void updateUserGraphStats() {
        statsView.setUserStatsGraphImage(GWT.getModuleBaseURL() + "api/stats/view/?" +
                "statsType=" + statsView.getUserStatsGraphSelection() +
                "&width=" + statsView.getUserStatsGraphWidthPx() +
                "&height=" + statsView.getUserStatsGrapheightPx() +
                "&dateOption=" + statsView.getUserStatsGraphDateOption()
        );
    }

    private void updateSupplierGraphStats() {
        statsView.setSupplierStatsGraphImage(GWT.getModuleBaseURL() + "api/stats/view/?" +
                "statsType=" + statsView.getSupplierStatsGraphSelection() +
                "&width=" + statsView.getSupplierStatsGraphWidthPx() +
                "&height=" + statsView.getSupplierStatsGrapheightPx() +
                "&dateOption=" + statsView.getSupplierStatsGraphDateOption()
        );
    }

    private void updateProductsGraphStats() {
        statsView.setProductsStatsGraphImage(GWT.getModuleBaseURL() + "api/stats/view/?" +
                "statsType=" + statsView.getProductsStatsGraphSelection() +
                "&width=" + statsView.getProductsStatsGraphWidthPx() +
                "&height=" + statsView.getProductsStatsGrapheightPx() +
                "&dateOption=" + statsView.getProductsStatsGraphDateOption()
        );
    }

    private void updatePlatformGraphStats() {
        statsView.setPlatformStatsGraphImage(GWT.getModuleBaseURL() + "api/stats/view/?" +
                "statsType=" + statsView.getPlatformStatsGraphSelection() +
                "&width=" + statsView.getPlatformStatsGraphWidthPx() +
                "&height=" + statsView.getPlatformStatsGrapheightPx() +
                "&dateOption=" + statsView.getPlatformStatsGraphDateOption()
        );
    }

}
