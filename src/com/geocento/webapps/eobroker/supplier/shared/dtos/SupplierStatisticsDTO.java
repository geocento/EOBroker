package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.feasibility.Statistics;

import java.util.List;

/**
 * Created by thomas on 14/06/2017.
 */
public class SupplierStatisticsDTO {

    List<Statistics> statistics;

    public SupplierStatisticsDTO() {
    }

    public List<Statistics> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Statistics> statistics) {
        this.statistics = statistics;
    }
}
