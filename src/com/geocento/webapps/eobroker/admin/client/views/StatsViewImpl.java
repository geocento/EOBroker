package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.shared.dtos.AdminStatisticsDTO;
import com.geocento.webapps.eobroker.admin.shared.dtos.STATS_GRAPHS;
import com.geocento.webapps.eobroker.common.client.widgets.charts.ChartWidget;
import com.geocento.webapps.eobroker.common.client.widgets.charts.StatsViewer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.googlecode.gwt.charts.client.geochart.GeoChart;
import com.googlecode.gwt.charts.client.geochart.GeoChartOptions;
import com.googlecode.gwt.charts.client.options.PieSliceText;
import com.googlecode.gwt.charts.client.table.Table;
import com.googlecode.gwt.charts.client.table.TableOptions;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialRow;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class StatsViewImpl extends Composite implements StatsView {

    interface DashboardViewUiBinder extends UiBinder<Widget, StatsViewImpl> {
    }

    private static DashboardViewUiBinder ourUiBinder = GWT.create(DashboardViewUiBinder.class);

    public static interface Style extends CssResource {
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    StatsViewer userGraphStats;
    @UiField
    StatsViewer supplierGraphStats;
    @UiField
    MaterialPanel userStats;
    @UiField
    MaterialPanel userCountryStats;
    @UiField
    MaterialPanel supplierStats;
    @UiField
    MaterialPanel supplierCountryStats;
    @UiField
    MaterialPanel offeringStats;
    @UiField
    MaterialPanel productFollowersStats;
    @UiField
    StatsViewer productGraphStats;
    @UiField
    MaterialButton platformReindex;
    @UiField
    MaterialRow platformCharts;
    @UiField
    MaterialPanel platformStats;
    @UiField
    MaterialPanel textSearch;
    @UiField
    StatsViewer platformGraphStats;

    private Presenter presenter;

    public StatsViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        template.setLink("statistics");

        userGraphStats.setCategories(new String[]{STATS_GRAPHS.userSignIn.toString(),
                        STATS_GRAPHS.userSignUp.toString(),
                        STATS_GRAPHS.userReset.toString(),
                        STATS_GRAPHS.userChangePassword.toString()
                },
                new String[]{"User sign-in",
                        "User sign up",
                        "User password reset",
                        "User password change"
                });

        supplierGraphStats.setCategories(new String[]{
                STATS_GRAPHS.supplierSignIn.toString(),
                        STATS_GRAPHS.supplierSignUp.toString()},
                new String[]{"Supplier sign-in",
                        "Supplier sign up"});

        supplierGraphStats.setCategories(new String[]{
                        STATS_GRAPHS.supplierSignIn.toString(),
                        STATS_GRAPHS.supplierSignUp.toString()},
                new String[]{"Supplier sign-in",
                        "Supplier sign up"});

        productGraphStats.setCategories(new String[]{
                        STATS_GRAPHS.productView.toString(),
                        STATS_GRAPHS.challengeView.toString()},
                new String[]{"Product category page views",
                        "Challenge page views"});

        platformGraphStats.setCategories(new String[]{
                        STATS_GRAPHS.cpuView.toString()
                },
                new String[]{"CPU usage"});

        // default duration selection to -3days
        userGraphStats.setDuration("-3days");
        supplierGraphStats.setDuration("-3days");
        productGraphStats.setDuration("-3days");
        platformGraphStats.setDuration("-3days");

    }

    @Override
    public void setStats(AdminStatisticsDTO adminStatisticsDTO) {
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART,
                ChartPackage.TABLE,
                ChartPackage.GEOCHART);
        chartLoader.loadApi(new Runnable() {
            @Override
            public void run() {
                // add user stats
                userStats.clear();
                {
                    HashMap<String, String> usersStatistics = adminStatisticsDTO.getUsersStatistics();
                    Table chart = new Table();
                    chart.setWidth("100%");
                    userStats.clear();
                    userStats.add(chart);
                    DataTable dataTable = ChartWidget.createDataTableString(usersStatistics, "User statistic", "Value");
                    TableOptions options = TableOptions.create();
                    options.setAlternatingRowStyle(true);
                    chart.draw(dataTable, options);
                }

                userCountryStats.clear();
                // add user country stats
                {
                    GeoChart geoChart = new GeoChart();
                    geoChart.setWidth("100%");
                    geoChart.setHeight("300px");
                    geoChart.setTitle("Number of users per country");
                    GeoChartOptions options = GeoChartOptions.create();
            /*
                    GeoChartColorAxis geoChartColorAxis = GeoChartColorAxis.create();
                    geoChartColorAxis.setColors(getNativeArray());
                    options.setColorAxis(geoChartColorAxis);
            */
                    DataTable usersPerCountry = ChartWidget.createDataTable(adminStatisticsDTO.getUsersPerCountry(), "Users", "Country");
                    geoChart.draw(usersPerCountry, options);
                    userCountryStats.add(geoChart);
                }
                // add supplier stats
                supplierStats.clear();
                {
                    HashMap<String, String> supplierStatistics = adminStatisticsDTO.getSupplierStatistics();
                    Table chart = new Table();
                    chart.setWidth("100%");
                    supplierStats.add(chart);
                    DataTable dataTable = ChartWidget.createDataTableString(supplierStatistics, "Suppliers statistic", "Value");
                    TableOptions options = TableOptions.create();
                    options.setAlternatingRowStyle(true);
                    chart.draw(dataTable, options);
                }

                supplierCountryStats.clear();
                // add user country stats
                {
                    GeoChart geoChart = new GeoChart();
                    geoChart.setWidth("100%");
                    geoChart.setHeight("300px");
                    geoChart.setTitle("Number of suppliers per country");
                    GeoChartOptions options = GeoChartOptions.create();
            /*
                    GeoChartColorAxis geoChartColorAxis = GeoChartColorAxis.create();
                    geoChartColorAxis.setColors(getNativeArray());
                    options.setColorAxis(geoChartColorAxis);
            */
                    DataTable suppliersPerCountry = ChartWidget.createDataTable(adminStatisticsDTO.getSuppliersPerCountry(), "Suppliers", "Country");
                    geoChart.draw(suppliersPerCountry, options);
                    supplierCountryStats.add(geoChart);
                }
                // add offering stats
                offeringStats.clear();
                {
                    HashMap<String, String> offeringStatistics = adminStatisticsDTO.getOfferingStatistics();
                    Table chart = new Table();
                    chart.setWidth("100%");
                    offeringStats.add(chart);
                    DataTable dataTable = ChartWidget.createDataTableString(offeringStatistics, "Offerings statistic", "Value");
                    TableOptions options = TableOptions.create();
                    options.setAlternatingRowStyle(true);
                    chart.draw(dataTable, options);
                }

                productFollowersStats.clear();
                // add user country stats
                {
                    PieChart followersStats = new PieChart();
                    followersStats.setWidth("100%");
                    followersStats.setHeight("100%");
                    DataTable dataTable = ChartWidget.createDataTable(adminStatisticsDTO.getProductFollowers(), "Labels", "Values");
                    PieChartOptions options = PieChartOptions.create();
                    options.setPieSliceText(PieSliceText.VALUE);
                    options.setTitle("Followers per product category (10 most followed)");
                    followersStats.draw(dataTable, options);
                    productFollowersStats.add(followersStats);
                }

                // add plaform stats
                platformStats.clear();
                {
                    HashMap<String, String> platformStatistics = adminStatisticsDTO.getPlatformStatistics();
                    Table chart = new Table();
                    chart.setWidth("100%");
                    platformStats.add(chart);
                    DataTable dataTable = ChartWidget.createDataTableString(platformStatistics, "Platform statistic", "Value");
                    TableOptions options = TableOptions.create();
                    options.setAlternatingRowStyle(true);
                    chart.draw(dataTable, options);
                }
            }});
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public HasValueChangeHandlers<String> getUserGraphStatsType() {
        return userGraphStats.getCategorySelection();
    }

    @Override
    public HasValueChangeHandlers<String> getUserGraphStatsDuration() {
        return userGraphStats.getDurationSelection();
    }

    @Override
    public String getUserStatsGraphSelection() {
        return userGraphStats.getCategorySelectionValue();
    }

    @Override
    public int getUserStatsGraphWidthPx() {
        return userGraphStats.getGraphWidth();
    }

    @Override
    public int getUserStatsGrapheightPx() {
        return userGraphStats.getGraphHeight();
    }

    @Override
    public String getUserStatsGraphDateOption() {
        return userGraphStats.getDurationSelectionValue();
    }

    @Override
    public void setUserStatsGraphImage(String url) {
        userGraphStats.setGraphImage(url);
    }

    @Override
    public HasValueChangeHandlers<String> getSupplierGraphStatsType() {
        return supplierGraphStats.getCategorySelection();
    }

    @Override
    public HasValueChangeHandlers<String> getSupplierGraphStatsDuration() {
        return supplierGraphStats.getDurationSelection();
    }

    @Override
    public String getSupplierStatsGraphSelection() {
        return supplierGraphStats.getCategorySelectionValue();
    }

    @Override
    public int getSupplierStatsGraphWidthPx() {
        return supplierGraphStats.getGraphWidth();
    }

    @Override
    public int getSupplierStatsGrapheightPx() {
        return supplierGraphStats.getGraphHeight();
    }

    @Override
    public String getSupplierStatsGraphDateOption() {
        return supplierGraphStats.getDurationSelectionValue();
    }

    @Override
    public void setSupplierStatsGraphImage(String url) {
        supplierGraphStats.setGraphImage(url);
    }

    @Override
    public HasClickHandlers getPlatformReindex() {
        return platformReindex;
    }

    @Override
    public HasValueChangeHandlers<String> getProductsGraphStatsType() {
        return productGraphStats.getCategorySelection();
    }

    @Override
    public HasValueChangeHandlers<String> getProductsGraphStatsDuration() {
        return productGraphStats.getDurationSelection();
    }

    @Override
    public String getProductsStatsGraphSelection() {
        return productGraphStats.getCategorySelectionValue();
    }

    @Override
    public int getProductsStatsGraphWidthPx() {
        return productGraphStats.getGraphWidth();
    }

    @Override
    public int getProductsStatsGrapheightPx() {
        return productGraphStats.getGraphHeight();
    }

    @Override
    public String getProductsStatsGraphDateOption() {
        return productGraphStats.getDurationSelectionValue();
    }

    @Override
    public void setProductsStatsGraphImage(String url) {
        productGraphStats.setGraphImage(url);
    }

    @Override
    public HasValueChangeHandlers<String> getPlatformGraphStatsType() {
        return platformGraphStats.getCategorySelection();
    }

    @Override
    public HasValueChangeHandlers<String> getPlatformGraphStatsDuration() {
        return platformGraphStats.getDurationSelection();
    }

    @Override
    public String getPlatformStatsGraphSelection() {
        return platformGraphStats.getCategorySelectionValue();
    }

    @Override
    public int getPlatformStatsGraphWidthPx() {
        return platformGraphStats.getGraphWidth();
    }

    @Override
    public int getPlatformStatsGrapheightPx() {
        return platformGraphStats.getGraphHeight();
    }

    @Override
    public String getPlatformStatsGraphDateOption() {
        return platformGraphStats.getDurationSelectionValue();
    }

    @Override
    public void setPlatformStatsGraphImage(String url) {
        platformGraphStats.setGraphImage(url);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}