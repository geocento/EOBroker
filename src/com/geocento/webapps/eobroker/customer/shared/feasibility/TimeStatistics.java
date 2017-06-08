package com.geocento.webapps.eobroker.customer.shared.feasibility;

import com.geocento.webapps.eobroker.common.shared.feasibility.TimeSerie;

import java.util.List;

/**
 * Created by thomas on 02/06/2017.
 */
public class TimeStatistics extends Statistics {

    String timeLabel;
    String valueLabel;
    List<TimeSerie> values;

    public TimeStatistics() {
    }

    public String getTimeLabel() {
        return timeLabel;
    }

    public void setTimeLabel(String timeLabel) {
        this.timeLabel = timeLabel;
    }

    public String getValueLabel() {
        return valueLabel;
    }

    public void setValueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
    }

    public List<TimeSerie> getValues() {
        return values;
    }

    public void setValues(List<TimeSerie> values) {
        this.values = values;
    }
}
