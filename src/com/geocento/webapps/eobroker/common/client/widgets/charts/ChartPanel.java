package com.geocento.webapps.eobroker.common.client.widgets.charts;

import com.geocento.webapps.eobroker.common.client.widgets.WidgetUtil;
import com.geocento.webapps.eobroker.common.shared.feasibility.BarChartStatistics;
import com.geocento.webapps.eobroker.common.shared.feasibility.PieChartStatistics;
import com.geocento.webapps.eobroker.common.shared.feasibility.Statistics;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.BarChart;
import com.googlecode.gwt.charts.client.corechart.BarChartOptions;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconSize;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialTooltip;

import java.util.Map;

/**
 * Created by thomas on 19/06/2017.
 */
public class ChartPanel extends MaterialPanel implements ResizeHandler {

    public ChartPanel() {
    }

    public void addPieChartStatistics(PieChartStatistics pieChartStatistics) {
        PieChart chart = new PieChart();
        chart.setWidth(getOffsetWidth() + "px");
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
            index++;
        }
        PieChartOptions options = PieChartOptions.create();
        chart.draw(dataTable, options);
    }

    public void addBarChartStatistics(BarChartStatistics barChartStatistics) {
        MaterialPanel materialPanel = new MaterialPanel();
        materialPanel.setLayoutPosition(Style.Position.RELATIVE);
        add(materialPanel);
        BarChart chart = new BarChart();
        chart.setWidth("100%");
        chart.setHeight("100%");
        chart.getElement().getStyle().setProperty("boxSizing", "border-box");
        materialPanel.add(chart);
        {
            MaterialLink viewLink = new MaterialLink();
            viewLink.setIconType(IconType.OPEN_IN_NEW);
            viewLink.setIconSize(IconSize.TINY);
            viewLink.setFloat(Style.Float.RIGHT);
            MaterialTooltip tooltip = new MaterialTooltip(viewLink, "Click to view in window");
            viewLink.setLayoutPosition(Style.Position.ABSOLUTE);
            viewLink.setBottom(5);
            viewLink.setRight(5);
            materialPanel.add(viewLink);
            viewLink.addClickHandler(event -> {
/*
                materialOverlay.clear();
                materialOverlay.add(chart);
                materialOverlay.add(new MaterialButton("Close"));
                materialOverlay.open(viewLink);
*/
                Window.alert("TODO - display in full screen");
            });
        }
        DataTable dataTable = DataTable.create();
        dataTable.addColumn(ColumnType.STRING, "Labels");
        dataTable.addColumn(ColumnType.NUMBER, "Values");
        Map<String, Double> values = barChartStatistics.getValues();
        dataTable.addRows(values.size());
        int index = 0;
        for(String label : values.keySet()) {
            dataTable.setValue(index, 0, label);
            dataTable.setValue(index, 1, values.get(label));
            index++;
        }
        BarChartOptions options = BarChartOptions.create();
        HAxis hAxis = HAxis.create();
        hAxis.setTitle(barChartStatistics.getxLabel());
        options.setHAxis(hAxis);
        VAxis vAxis = VAxis.create();
        vAxis.setTitle(barChartStatistics.getyLabel());
        options.setVAxis(vAxis);
        chart.draw(dataTable, options);
    }

    public void loadChartAPI(Runnable runnable) {
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
        chartLoader.loadApi(runnable);
    }

    public void addStatistics(Statistics statistics) {
        MaterialPanel materialPanel = new MaterialPanel();
        String name = statistics.getName();
        String description = statistics.getDescription();
        MaterialLink materialLink = new MaterialLink(name);
        materialLink.setTextColor(Color.BLACK);
        materialLink.setFontSize("0.9em");
        materialLink.setPaddingBottom(10);
        //materialLink.setIconType(getIconType(statistics));
        materialPanel.add(materialLink);
        if(description != null) {
            MaterialLink infoLink = new MaterialLink();
            infoLink.setIconType(IconType.INFO);
            infoLink.setIconSize(IconSize.TINY);
            infoLink.setFloat(Style.Float.RIGHT);
            MaterialTooltip tooltip = new MaterialTooltip(infoLink, description);
            materialPanel.add(infoLink);
        }
        add(materialPanel);
        if(statistics instanceof PieChartStatistics) {
            addPieChartStatistics((PieChartStatistics) statistics);
        } else if(statistics instanceof BarChartStatistics) {
            addBarChartStatistics((BarChartStatistics) statistics);
        }
    }

    private IconType getIconType(Statistics statistics) {
        if(statistics instanceof PieChartStatistics) {
            return IconType.PIE_CHART;
        } else if(statistics instanceof BarChartStatistics) {
            return IconType.SIGNAL_WIFI_4_BAR_LOCK;
        } else {
            return null;
        }
    }

    @Override
    public void onResize(ResizeEvent event) {
        WidgetUtil.performAction(this, new WidgetUtil.Action() {
            @Override
            public void action(Widget widget) {
                if(widget instanceof ResizeHandler) {
                    ((ResizeHandler) widget).onResize(event);
                }
            }
        });
    }
}
