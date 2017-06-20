package com.geocento.webapps.eobroker.common.client.widgets.charts;

import com.geocento.webapps.eobroker.common.shared.feasibility.PieChartStatistics;
import com.geocento.webapps.eobroker.common.shared.feasibility.Statistics;
import com.geocento.webapps.eobroker.customer.client.widgets.PieOpt;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import gwt.material.design.client.ui.MaterialPanel;

import java.util.Map;

/**
 * Created by thomas on 19/06/2017.
 */
public class ChartWidget extends MaterialPanel {

    public ChartWidget() {
    }

    public void addPieChartStatistics(PieChartStatistics pieChartStatistics) {
        PieChart chart = new PieChart();
        chart.setWidth("100%");
        chart.setHeight("100%");
        add(chart);
        DataTable dataTable = DataTable.create();
        dataTable.addColumn(ColumnType.STRING, "Labels");
        dataTable.addColumn(ColumnType.NUMBER, "Values");
        Map<String, Double> values = pieChartStatistics.getValues();
        dataTable.addRows(values.size());
        int index = 0;
        for(String label : values.keySet()) {
            dataTable.setValue(index, 0, label);
            dataTable.setValue(index, 1, values.get(label));
        }
        PieOpt opt = new PieOpt();
        //opt.setColors("2196f3", "42a5f5", "64b5f6", "90caf9", "bbdefb");

        chart.draw(dataTable, opt.get());
    }

    public void loadChartAPI(Runnable runnable) {
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
        chartLoader.loadApi(runnable);
    }

    public void addStatistics(Statistics statistics) {
        if(statistics instanceof PieChartStatistics) {
            addPieChartStatistics((PieChartStatistics) statistics);
        }
    }
}
