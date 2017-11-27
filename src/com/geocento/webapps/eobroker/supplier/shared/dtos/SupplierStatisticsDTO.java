package com.geocento.webapps.eobroker.supplier.shared.dtos;

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
}
