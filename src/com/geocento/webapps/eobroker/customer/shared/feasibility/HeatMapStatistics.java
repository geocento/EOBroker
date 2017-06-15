package com.geocento.webapps.eobroker.customer.shared.feasibility;

import java.util.List;

/**
 * Created by thomas on 15/06/2017.
 */
public abstract class HeatMapStatistics extends Statistics {

    List<LocationValue> values;

    public HeatMapStatistics() {
    }

    /**
     *
     * list of location values to build a heat map in the client
     *
     * @return
     */
    public List<LocationValue> getValues() {
        return values;
    }

    public void setValues(List<LocationValue> values) {
        this.values = values;
    }
}
