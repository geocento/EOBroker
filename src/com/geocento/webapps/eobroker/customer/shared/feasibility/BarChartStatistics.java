package com.geocento.webapps.eobroker.customer.shared.feasibility;

/**
 * Created by thomas on 15/06/2017.
 */
public class BarChartStatistics extends ValueStatistics {

    String xLabel;
    String yLabel;

    public BarChartStatistics() {
    }

    public String getxLabel() {
        return xLabel;
    }

    public void setxLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public String getyLabel() {
        return yLabel;
    }

    public void setyLabel(String yLabel) {
        this.yLabel = yLabel;
    }
}
