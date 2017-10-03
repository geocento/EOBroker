package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by thomas on 08/11/2016.
 */
public class SelectLayerWidget extends Composite {

    interface SelectLayerWidgetUiBinder extends UiBinder<Widget, SelectLayerWidget> {
    }

    private static SelectLayerWidgetUiBinder ourUiBinder = GWT.create(SelectLayerWidgetUiBinder.class);

    @UiField
    MaterialButton select;
    @UiField
    MaterialLabel name;
    @UiField
    MaterialPanel selectPanel;
    @UiField
    MaterialButton delete;

    private final DatasetAccessOGC datasetAccessOGC;

    public SelectLayerWidget(DatasetAccessOGC datasetAccessOGC) {
        this.datasetAccessOGC = datasetAccessOGC;
        initWidget(ourUiBinder.createAndBindUi(this));
        updateName();
    }

    public DatasetAccessOGC getDatasetAccessOGC() {
        return datasetAccessOGC;
    }

    private void updateName() {
        String name = datasetAccessOGC.getTitle();
        name = name == null || name.length() == 0 ? "No name" : name;
        this.name.setText(name);
    }

    public HasClickHandlers getSelect() {
        return select;
    }

    public HasClickHandlers getDelete() {
        return delete;
    }

    public void enableDelete(boolean enabled) {
        delete.setVisible(enabled);
    }

}