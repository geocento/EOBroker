package com.geocento.webapps.eobroker.common.client.widgets.charts;

import com.geocento.webapps.eobroker.common.client.utils.DateUtil;
import com.geocento.webapps.eobroker.common.client.widgets.WidgetUtil;
import com.geocento.webapps.eobroker.common.shared.feasibility.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.*;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialTooltip;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by thomas on 26/06/2017.
 */
public class ChartWidget extends Composite implements ResizeHandler {

    interface ChartWidgetUiBinder extends UiBinder<Widget, ChartWidget> {
    }

    private static ChartWidgetUiBinder ourUiBinder = GWT.create(ChartWidgetUiBinder.class);
    @UiField
    MaterialLink viewLink;
    @UiField
    MaterialLink descriptionLink;
    @UiField
    MaterialPanel chartPanel;
    @UiField
    MaterialTooltip description;
    @UiField
    MaterialLink label;

    public ChartWidget(Statistics statistics) {

        initWidget(ourUiBinder.createAndBindUi(this));

        label.setText(statistics.getName());
        boolean hasDescription = statistics.getDescription() != null;
        descriptionLink.setVisible(hasDescription);
        if(hasDescription) {
            description.setText(statistics.getDescription());
        }
        viewLink.addClickHandler(event -> {
            Window.alert("TODO - display in full screen");
        });

        if(statistics instanceof PieChartStatistics) {
            setPieChartStatistics((PieChartStatistics) statistics);
        } else if(statistics instanceof BarChartStatistics) {
            setBarChartStatistics((BarChartStatistics) statistics);
        } else if(statistics instanceof TimeGanttStatistics) {
            setTimeGanttChartStatistics((TimeGanttStatistics) statistics);
        }
    }

    private void setTimeGanttChartStatistics(TimeGanttStatistics timeGanttStatistics) {
        TimeGrid timeGrid = new TimeGrid();
        timeGrid.setHeight("25px");
        // resize later
        timeGrid.setWidth("100%");
        timeGrid.getElement().getStyle().setMarginBottom(30, Style.Unit.PX);
        timeGrid.clearAll();
        List<Date> dates = new ArrayList<Date>();
        // find min and max of time series
        Date startDate = null;
        Date stopDate = null;
        for(TimePoint timePoint : timeGanttStatistics.getValues()) {
            Date dateValue = timePoint.getDate();
            if(startDate == null || startDate.after(dateValue)) {
                startDate = dateValue;
            }
            if(stopDate == null || stopDate.before(dateValue)) {
                stopDate = dateValue;
            }
            dates.add(dateValue);
        }
        double duration = Math.max(DateUtil.dayInMs, stopDate.getTime() - startDate.getTime());
        // add a bit of margin
        Date minDate = new Date(startDate.getDate() - (long) (0.1 * duration));
        Date maxDate = new Date(stopDate.getDate() + (long) (0.1 * duration));
        timeGrid.setTimeFrame(minDate, maxDate);
        timeGrid.setDates(dates);
        setChartWidget(timeGrid);
    }

    public void setPieChartStatistics(PieChartStatistics pieChartStatistics) {
        PieChart chart = new PieChart();
        chart.setWidth("100%");
        chart.setHeight("100%");
        DataTable dataTable = createDataTable(pieChartStatistics.getValues(), "Labels", "Values");
        PieChartOptions options = PieChartOptions.create();
        chart.draw(dataTable, options);
        setChartWidget(chart);
    }

    private void setChartWidget(Widget chart) {
        chartPanel.clear();
        chartPanel.add(chart);
    }

    public static DataTable createDataTable(Map<String, Double> values, String textLabel, String valueLabel) {
        DataTable dataTable = DataTable.create();
        dataTable.addColumn(ColumnType.STRING, textLabel);
        dataTable.addColumn(ColumnType.NUMBER, valueLabel);
        dataTable.addRows(values.size());
        int index = 0;
        for(String label : values.keySet()) {
            dataTable.setValue(index, 0, label);
            dataTable.setValue(index, 1, values.get(label));
            index++;
        }
        return dataTable;
    }

    public static DataTable createDataTableString(Map<String, String> values, String textLabel, String valueLabel) {
        DataTable dataTable = DataTable.create();
        dataTable.addColumn(ColumnType.STRING, textLabel);
        dataTable.addColumn(ColumnType.STRING, valueLabel);
        dataTable.addRows(values.size());
        int index = 0;
        for(String label : values.keySet()) {
            dataTable.setValue(index, 0, label);
            dataTable.setValue(index, 1, values.get(label));
            index++;
        }
        return dataTable;
    }

    public void setBarChartStatistics(BarChartStatistics barChartStatistics) {
        CoreChartWidget chart = barChartStatistics.isVertical() ? new ColumnChart() : new BarChart();
        chart.setWidth("100%");
        chart.setHeight("100%");
        chart.getElement().getStyle().setProperty("boxSizing", "border-box");
        DataTable dataTable = createDataTable(barChartStatistics.getValues(),"Labels", "Values");
        BarChartOptions options = BarChartOptions.create();
        HAxis hAxis = HAxis.create();
        hAxis.setTitle(barChartStatistics.getxLabel());
        options.setHAxis(hAxis);
        VAxis vAxis = VAxis.create();
        vAxis.setTitle(barChartStatistics.getyLabel());
        options.setVAxis(vAxis);
        chart.draw(dataTable, options);
        setChartWidget(chart);
    }

    @Override
    public void onResize(ResizeEvent event) {
        WidgetUtil.performAction(chartPanel, widget -> {
            if (widget instanceof ResizeHandler) {
                ((ResizeHandler) widget).onResize(event);
            }
        });
    }

}