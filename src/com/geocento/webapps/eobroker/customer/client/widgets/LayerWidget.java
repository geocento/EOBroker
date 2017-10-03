package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCheckBox;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialPreLoader;

/**
 * Created by thomas on 02/10/2017.
 */
public class LayerWidget extends Composite {

    interface LayerWidgetUiBinder extends UiBinder<Widget, LayerWidget> {
    }

    private static LayerWidgetUiBinder ourUiBinder = GWT.create(LayerWidgetUiBinder.class);

    @UiField
    MaterialCheckBox layerSelection;
    @UiField
    MaterialPreLoader loader;
    @UiField
    MaterialIcon extent;

    private final DatasetAccessOGC datasetAccessOGC;

    public LayerWidget(DatasetAccessOGC datasetAccessOGC) {

        this.datasetAccessOGC = datasetAccessOGC;

        initWidget(ourUiBinder.createAndBindUi(this));

        layerSelection.setText(datasetAccessOGC.getTitle());

        setLoading(false);
    }

    public HasValueChangeHandlers<Boolean> getSelectionHandler() {
        return layerSelection;
    }

    public void setLoading(boolean loading) {
        loader.setVisible(loading);
    }

    public HasValue<Boolean> getSelection() {
        return layerSelection;
    }

    public HasClickHandlers getExtentButton() {
        return extent;
    }

    public DatasetAccessOGC getDatasetAccessOGC() {
        return datasetAccessOGC;
    }
}