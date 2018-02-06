package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.client.widgets.charts.ChartPanel;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.feasibility.PieChartStatistics;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierStatisticsDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialListBox;
import gwt.material.design.client.ui.MaterialListValueBox;
import gwt.material.design.client.ui.MaterialPanel;

import java.util.ArrayList;
import java.util.Collections;
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
    @UiField
    MaterialListBox searchCategory;
    @UiField
    MaterialListBox searchStatsDateOptions;
    @UiField
    MaterialImageLoading searchStats;
    @UiField
    MaterialImageLoading productsStats;
    @UiField
    MaterialListBox productsStatsDateOptions;
    @UiField
    MaterialListBox productsCategory;
    @UiField
    MaterialPanel offeringsStats;

    public StatisticsViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        populateDateOptions(productsStatsDateOptions);
        populateDateOptions(viewStatsDateOptions);
        populateDateOptions(searchStatsDateOptions);

        template.setPlace(null);
    }

    private void populateDateOptions(MaterialListValueBox<String> dateOptions) {
        dateOptions.addItem("-10mins", "10 minutes");
        dateOptions.addItem("-1hours", "1 hour");
        dateOptions.addItem("-6hours", "6 hours");
        dateOptions.addItem("-12hours", "12 hours");
        dateOptions.addItem("-24hours", "24 hours");
        dateOptions.addItem("-3days", "3 days");
        dateOptions.addItem("-7days", "7 days");
        dateOptions.addItem("-1months", "1 month");
        dateOptions.addItem("-3months", "3 months");
        dateOptions.addItem("-6months", "6 months");
        dateOptions.addItem("-1years", "1 year");
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
        offeringsStats.add(new HTMLPanel("" +
                addProperty("Number of off the shelf products", supplierStatisticsDTO.getOfferingsStats().get(Category.productdatasets)) +
                addProperty("Number of bespoke services", supplierStatisticsDTO.getOfferingsStats().get(Category.productservices)) +
                addProperty("Number of software solutions", supplierStatisticsDTO.getOfferingsStats().get(Category.software)) +
                addProperty("Number of projects", supplierStatisticsDTO.getOfferingsStats().get(Category.project)) +
                addProperty("Number of product categories covered", supplierStatisticsDTO.getViewProductsOptions().size()) +
                addProperty("Number of followers", supplierStatisticsDTO.getNumberOfFollowers())
        ));
        chartPanel.clear();
        chartPanel.loadChartAPI(new Runnable() {
            @Override
            public void run() {
                PieChartStatistics pieChartStatistics = new PieChartStatistics();
                //barChartStatistics.setName("Product followers");
                pieChartStatistics.setDescription("Number of followers for each product category");
                pieChartStatistics.setName("Followers");
                pieChartStatistics.setValues(supplierStatisticsDTO.getProductFollowers());
                //barChartStatistics.setVertical(true);
                chartPanel.addStatistics(pieChartStatistics);
            }
        });
        HashMap<String, String> productsViewStatsOptions = supplierStatisticsDTO.getViewProductsOptions();
        if(productsViewStatsOptions != null) {
            List<String> options = new ArrayList<String>(productsViewStatsOptions.keySet());
            Collections.sort(options);
            for (String viewName : options) {
                productsCategory.addItem(productsViewStatsOptions.get(viewName), viewName);
            }
        }
        HashMap<String, String> viewStatsOptions = supplierStatisticsDTO.getViewStatsOptions();
        if(viewStatsOptions != null) {
            List<String> options = new ArrayList<String>(viewStatsOptions.keySet());
            Collections.sort(options);
            for (String viewName : options) {
                viewCategory.addItem(viewStatsOptions.get(viewName), viewName);
            }
        }
        HashMap<String, String> searchStatsOptions = supplierStatisticsDTO.getSearchStatsOptions();
        if(searchStatsOptions != null) {
            List<String> options = new ArrayList<String>(searchStatsOptions.keySet());
            Collections.sort(options);
            for (String statsName : options) {
                searchCategory.addItem(searchStatsOptions.get(statsName), statsName);
            }
        }
    }

    private String addProperty(String name, Integer value) {
        return "<p><b>" + name + "</b>: " + (value == null || value == 0 ? "none" : value) + "</p>";
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

    @Override
    public HasChangeHandlers getSearchStatsOptions() {
        return searchCategory.getListBox();
    }

    @Override
    public List<String> getSelectedSearchStatsOptions() {
        return ListUtil.toList(searchCategory.getItemsSelected());
    }

    @Override
    public void setSearchStatsImage(String imageUrl) {
        searchStats.setImageUrl(imageUrl);
    }

    @Override
    public void displaySearchStatsLoading(String message) {
        // nothing to do...
    }

    @Override
    public int getSearchStatsWidthPx() {
        return searchStats.getOffsetWidth();
    }

    @Override
    public int getSearchStatsHeightPx() {
        return searchStats.getOffsetHeight();
    }

    @Override
    public String getSearchStatsDateOption() {
        return searchStatsDateOptions.getValue();
    }

    @Override
    public HasChangeHandlers getSearchStatsDateOptions() {
        return searchStatsDateOptions.getListBox();
    }

    @Override
    public HasChangeHandlers getProductsStatsOptions() {
        return productsCategory.getListBox();
    }

    @Override
    public List<String> getSelectedProductsStatsOptions() {
        return ListUtil.toList(productsCategory.getItemsSelected());
    }

    @Override
    public void setProductsStatsImage(String imageUrl) {
        productsStats.setImageUrl(imageUrl);
    }

    @Override
    public void displayProductsStatsLoading(String message) {
        // nothing to do...
    }

    @Override
    public int getProductsStatsWidthPx() {
        return productsStats.getOffsetWidth();
    }

    @Override
    public int getProductsStatsHeightPx() {
        return productsStats.getOffsetHeight();
    }

    @Override
    public String getProductsStatsDateOption() {
        return productsStatsDateOptions.getValue();
    }

    @Override
    public HasChangeHandlers getProductsStatsDateOptions() {
        return productsStatsDateOptions.getListBox();
    }

}