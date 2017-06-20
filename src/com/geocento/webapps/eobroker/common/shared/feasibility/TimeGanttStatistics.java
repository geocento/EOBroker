package com.geocento.webapps.eobroker.common.shared.feasibility;

import java.util.List;

/**
 * Created by thomas on 02/06/2017.
 */
public class TimeGanttStatistics extends Statistics {

    String timeLabel;
    String valueLabel;
    List<TimePoint> values;

    public TimeGanttStatistics() {
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

    public List<TimePoint> getValues() {
        return values;
    }

    public void setValues(List<TimePoint> values) {
        this.values = values;
    }
}
