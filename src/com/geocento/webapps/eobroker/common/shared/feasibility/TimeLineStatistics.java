package com.geocento.webapps.eobroker.common.shared.feasibility;

import java.util.List;

/**
 * Created by thomas on 02/06/2017.
 */
public class TimeLineStatistics extends Statistics {

    String timeLabel;
    String valueLabel;
    List<TimeValue> values;

    public TimeLineStatistics() {
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

    public List<TimeValue> getValues() {
        return values;
    }

    public void setValues(List<TimeValue> values) {
        this.values = values;
    }
}
