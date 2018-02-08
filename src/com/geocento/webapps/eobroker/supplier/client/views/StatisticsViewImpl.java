package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.charts.ChartWidget;
import com.geocento.webapps.eobroker.common.client.widgets.charts.StatsViewer;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierStatisticsDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.googlecode.gwt.charts.client.options.PieSliceText;
import com.googlecode.gwt.charts.client.table.Table;
import com.googlecode.gwt.charts.client.table.TableOptions;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialRow;

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
    MaterialPanel followersStats;
    @UiField
    MaterialPanel offeringsStats;
    @UiField
    MaterialRow charts;
    @UiField
    StatsViewer offeringsGraphStats;
    @UiField
    StatsViewer productsGraphStats;
    @UiField
    StatsViewer searchGraphStats;

    public StatisticsViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        productsGraphStats.setDuration("-3days");
        offeringsGraphStats.setDuration("-3days");
        searchGraphStats.setDuration("-3days");
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
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART,
                ChartPackage.TABLE,
                ChartPackage.GEOCHART);
        chartLoader.loadApi(new Runnable() {
            @Override
            public void run() {
                // add the numbers on offerings
                {
                    HashMap<String, Double> values = new HashMap<String, Double>();
                    values.put("off the shelf products", Double.valueOf(supplierStatisticsDTO.getOfferingsStats().get(Category.productdatasets)));
                    values.put("bespoke services", Double.valueOf(supplierStatisticsDTO.getOfferingsStats().get(Category.productservices)));
                    values.put("software solutions", Double.valueOf(supplierStatisticsDTO.getOfferingsStats().get(Category.software)));
                    values.put("Number of projects", Double.valueOf(supplierStatisticsDTO.getOfferingsStats().get(Category.project)));
                    values.put("Number of product categories covered", Double.valueOf(supplierStatisticsDTO.getViewProductsOptions().size()));
                    values.put("Number of followers", Double.valueOf(supplierStatisticsDTO.getNumberOfFollowers()));
                    Table chart = new Table();
                    chart.setWidth("100%");
                    offeringsStats.clear();
                    offeringsStats.add(chart);
                    DataTable dataTable = ChartWidget.createDataTable(values, "Type of Offering", "Number registered");
                    TableOptions options = TableOptions.create();
                    options.setAlternatingRowStyle(true);
                    chart.draw(dataTable, options);
                }

                // add the stats on followers
                {
                    PieChart chart = new PieChart();
                    chart.setWidth("100%");
                    chart.setHeight("100%");
                    followersStats.clear();
                    followersStats.add(chart);
                    DataTable dataTable = ChartWidget.createDataTable(supplierStatisticsDTO.getProductFollowers(), "Labels", "Values");
                    PieChartOptions options = PieChartOptions.create();
                    options.setPieSliceText(PieSliceText.VALUE);
                    options.setTitle("Followers per product category");
                    chart.draw(dataTable, options);
                }
            }
        });
        HashMap<String, String> productsViewStatsOptions = supplierStatisticsDTO.getViewProductsOptions();
        productsGraphStats.setCategories(productsViewStatsOptions);
        HashMap<String, String> viewStatsOptions = supplierStatisticsDTO.getViewStatsOptions();
        offeringsGraphStats.setCategories(viewStatsOptions);
        HashMap<String, String> searchStatsOptions = supplierStatisticsDTO.getSearchStatsOptions();
        searchGraphStats.setCategories(searchStatsOptions);
    }

    @Override
    public HasValueChangeHandlers<String> getViewStatsOptions() {
        return offeringsGraphStats.getCategorySelection();
    }

    @Override
    public List<String> getSelectedViewStatsOptions() {
        return ListUtil.toList(offeringsGraphStats.getCategorySelectionValue());
    }

    @Override
    public void setViewStatsImage(String imageUrl) {
        offeringsGraphStats.setGraphImage(imageUrl);
    }

    @Override
    public void displayViewStatsLoading(String message) {
        // nothing to do...
    }

    @Override
    public int getViewStatsWidthPx() {
        return offeringsGraphStats.getGraphWidth();
    }

    @Override
    public int getViewStatsHeightPx() {
        return offeringsGraphStats.getGraphHeight();
    }

    @Override
    public String getViewStatsDateOption() {
        return offeringsGraphStats.getDurationSelectionValue();
    }

    @Override
    public HasValueChangeHandlers<String> getViewStatsDateOptions() {
        return offeringsGraphStats.getDurationSelection();
    }

    @Override
    public HasValueChangeHandlers<String> getProductsStatsOptions() {
        return productsGraphStats.getCategorySelection();
    }

    @Override
    public List<String> getSelectedProductsStatsOptions() {
        return ListUtil.toList(productsGraphStats.getCategorySelectionValue());
    }

    @Override
    public void setProductsStatsImage(String imageUrl) {
        productsGraphStats.setGraphImage(imageUrl);
    }

    @Override
    public void displayProductsStatsLoading(String message) {

    }

    @Override
    public int getProductsStatsWidthPx() {
        return productsGraphStats.getGraphWidth();
    }

    @Override
    public int getProductsStatsHeightPx() {
        return productsGraphStats.getGraphHeight();
    }

    @Override
    public String getProductsStatsDateOption() {
        return productsGraphStats.getDurationSelectionValue();
    }

    @Override
    public HasValueChangeHandlers<String> getProductsStatsDateOptions() {
        return productsGraphStats.getDurationSelection();
    }

    @Override
    public HasValueChangeHandlers<String> getSearchStatsOptions() {
        return searchGraphStats.getCategorySelection();
    }

    @Override
    public List<String> getSelectedSearchStatsOptions() {
        return ListUtil.toList(searchGraphStats.getCategorySelectionValue());
    }

    @Override
    public void setSearchStatsImage(String imageUrl) {
        searchGraphStats.setGraphImage(imageUrl);
    }

    @Override
    public void displaySearchStatsLoading(String message) {
        // nothing to do...
    }

    @Override
    public int getSearchStatsWidthPx() {
        return searchGraphStats.getGraphWidth();
    }

    @Override
    public int getSearchStatsHeightPx() {
        return searchGraphStats.getGraphHeight();
    }

    @Override
    public String getSearchStatsDateOption() {
        return searchGraphStats.getDurationSelectionValue();
    }

    @Override
    public HasValueChangeHandlers<String> getSearchStatsDateOptions() {
        return searchGraphStats.getDurationSelection();
    }

}