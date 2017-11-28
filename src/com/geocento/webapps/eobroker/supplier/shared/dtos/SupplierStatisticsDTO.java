package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.feasibility.Statistics;

import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 14/06/2017.
 */
public class SupplierStatisticsDTO {

    List<Statistics> statistics;
    HashMap<String, String> viewStatsOptions;
    HashMap<String, String> searchStatsOptions;
    private HashMap<Category, Integer> offeringsStats;
    private HashMap<String, String> viewProductsOptions;
    private Integer numberOfFollowers;
    private HashMap<String, Double> productFollowers;

    public SupplierStatisticsDTO() {
    }

    public List<Statistics> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Statistics> statistics) {
        this.statistics = statistics;
    }

    public HashMap<String, String> getViewStatsOptions() {
        return viewStatsOptions;
    }

    public void setViewStatsOptions(HashMap<String, String> viewStatsOptions) {
        this.viewStatsOptions = viewStatsOptions;
    }

    public HashMap<String, String> getSearchStatsOptions() {
        return searchStatsOptions;
    }

    public void setSearchStatsOptions(HashMap<String, String> searchStatsOptions) {
        this.searchStatsOptions = searchStatsOptions;
    }

    public void setOfferingsStats(HashMap<Category, Integer> offeringsStats) {
        this.offeringsStats = offeringsStats;
    }

    public HashMap<Category, Integer> getOfferingsStats() {
        return offeringsStats;
    }

    public void setViewProductsOptions(HashMap<String, String> viewProductsOptions) {
        this.viewProductsOptions = viewProductsOptions;
    }

    public HashMap<String, String> getViewProductsOptions() {
        return viewProductsOptions;
    }

    public Integer getNumberOfFollowers() {
        return numberOfFollowers;
    }

    public void setNumberOfFollowers(Integer numberOfFollowers) {
        this.numberOfFollowers = numberOfFollowers;
    }

    public void setProductFollowers(HashMap<String, Double> productFollowers) {
        this.productFollowers = productFollowers;
    }

    public HashMap<String, Double> getProductFollowers() {
        return productFollowers;
    }
}
