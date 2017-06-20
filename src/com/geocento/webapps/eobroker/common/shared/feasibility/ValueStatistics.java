package com.geocento.webapps.eobroker.common.shared.feasibility;

import java.util.Map;

/**
 * Created by thomas on 15/06/2017.
 */
public abstract class ValueStatistics extends Statistics {

    Map<String, Double> values;

    public ValueStatistics() {
    }

    /**
     * object of label and value pairs for a chart display
     *
     * @return
     */
    public Map<String, Double> getValues() {
        return values;
    }

    public void setValues(Map<String, Double> values) {
        this.values = values;
    }
}
