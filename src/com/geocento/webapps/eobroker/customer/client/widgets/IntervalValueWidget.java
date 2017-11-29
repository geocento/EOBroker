package com.geocento.webapps.eobroker.customer.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import gwt.material.design.client.ui.MaterialDoubleBox;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by thomas on 29/11/2017.
 */
public class IntervalValueWidget extends MaterialPanel {

    interface IntervalValueWidgetUiBinder extends UiBinder<MaterialPanel, IntervalValueWidget> {
    }

    private static IntervalValueWidgetUiBinder ourUiBinder = GWT.create(IntervalValueWidgetUiBinder.class);

    static public interface Presenter {

    }

    @UiField
    MaterialLabel label;
    @UiField
    MaterialDoubleBox minValue;
    @UiField
    MaterialDoubleBox maxValue;

    private Presenter presenter;

    public IntervalValueWidget() {

        add(ourUiBinder.createAndBindUi(this));

    }

    public void setText(String name) {
        label.setText(name);
    }

    public Double getMinValue() {
        return minValue.getValue();
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}