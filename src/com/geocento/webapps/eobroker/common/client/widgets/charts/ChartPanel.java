package com.geocento.webapps.eobroker.common.client.widgets.charts;

import com.geocento.webapps.eobroker.common.client.widgets.WidgetUtil;
import com.geocento.webapps.eobroker.common.shared.feasibility.Statistics;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by thomas on 19/06/2017.
 */
public class ChartPanel extends MaterialPanel implements ResizeHandler {

    public ChartPanel() {
    }

    public void loadChartAPI(Runnable runnable) {
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
        chartLoader.loadApi(runnable);
    }

    public void addStatistics(Statistics statistics) {
        add(new ChartWidget(statistics));
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
