package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.PerformanceDescription;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialTextBox;

/**
 * Created by thomas on 08/11/2016.
 */
public class PerformanceWidget extends Composite {

    interface PerformanceWidgetUiBinder extends UiBinder<Widget, PerformanceWidget> {
    }

    private static PerformanceWidgetUiBinder ourUiBinder = GWT.create(PerformanceWidgetUiBinder.class);

    @UiField
    MaterialTextBox name;
    @UiField
    MaterialTextBox description;
    @UiField
    MaterialTextBox unit;
    @UiField
    MaterialButton remove;

    private final PerformanceDescription performanceDescription;

    public PerformanceWidget(PerformanceDescription performanceDescription) {

        this.performanceDescription = performanceDescription;

        initWidget(ourUiBinder.createAndBindUi(this));

        name.setText(performanceDescription.getName());
        description.setText(performanceDescription.getDescription());
        unit.setText(performanceDescription.getUnit());
    }

    public PerformanceDescription getPerformanceDescription() {
        // update values first
        performanceDescription.setName(name.getText());
        performanceDescription.setDescription(description.getText());
        performanceDescription.setUnit(unit.getText());
        return performanceDescription;
    }

    public HasClickHandlers getRemove() {
        return remove;
    }

}