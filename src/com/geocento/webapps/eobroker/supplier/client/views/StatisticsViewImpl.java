package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.client.widgets.charts.ChartPanel;
import com.geocento.webapps.eobroker.common.shared.feasibility.Statistics;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierStatisticsDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialListValueBox;

import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class StatisticsViewImpl extends Composite implements StatisticsView {

    private Presenter presenter;

    interface StatisticsViewUiBinder extends UiBinder<Widget, StatisticsViewImpl> {
    }

    private static StatisticsViewUiBinder ourUiBinder = GWT.create(StatisticsViewUiBinder.class);

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialImageLoading viewStats;
    @UiField
    MaterialListValueBox<String> viewCategory;
    @UiField
    MaterialListValueBox<String> viewStatsDateOptions;
    @UiField
    ChartPanel chartPanel;

    public StatisticsViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        viewStatsDateOptions.addItem("-10mins", "10 minutes");
        viewStatsDateOptions.addItem("-1hours", "1 hour");
        viewStatsDateOptions.addItem("-6hours", "6 hours");
        viewStatsDateOptions.addItem("-12hours", "12 hours");
        viewStatsDateOptions.addItem("-24hours", "24 hours");
        viewStatsDateOptions.addItem("-3days", "3 days");
        viewStatsDateOptions.addItem("-7days", "7 days");
        viewStatsDateOptions.addItem("-1months", "1 month");
        viewStatsDateOptions.addItem("-3months", "3 months");
        viewStatsDateOptions.addItem("-6months", "6 months");
        viewStatsDateOptions.addItem("-1years", "1 year");

        template.setPlace(null);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void displayStatistics(SupplierStatisticsDTO supplierStatisticsDTO) {
        chartPanel.clear();
        chartPanel.loadChartAPI(new Runnable() {
            @Override
            public void run() {
                for(Statistics statistic : supplierStatisticsDTO.getStatistics()) {
                    chartPanel.addStatistics(statistic);
                }
            }
        });
        HashMap<String, String> viewStatsOptions = supplierStatisticsDTO.getViewStatsOptions();
        if(viewStatsOptions != null) {
            for (String viewName : viewStatsOptions.keySet()) {
                viewCategory.addItem(viewStatsOptions.get(viewName), viewName);
            }
        }
    }

    @Override
    public HasChangeHandlers getViewStatsOptions() {
        return viewCategory.getListBox();
    }

    @Override
    public List<String> getSelectedViewStatsOptions() {
        return ListUtil.toList(viewCategory.getItemsSelected());
    }

    @Override
    public void setViewStatsImage(String imageUrl) {
        viewStats.setImageUrl(imageUrl);
    }

    @Override
    public void displayViewStatsLoading(String message) {
        // nothing to do...
    }

    @Override
    public int getViewStatsWidthPx() {
        return viewStats.getOffsetWidth();
    }

    @Override
    public int getViewStatsHeightPx() {
        return viewStats.getOffsetHeight();
    }

    @Override
    public String getViewStatsDateOption() {
        return viewStatsDateOptions.getValue();
    }

    @Override
    public HasChangeHandlers getViewStatsDateOptions() {
        return viewStatsDateOptions.getListBox();
    }

}