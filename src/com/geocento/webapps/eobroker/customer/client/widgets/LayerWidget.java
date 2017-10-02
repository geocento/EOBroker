package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import gwt.material.design.client.ui.MaterialCheckBox;

/**
 * Created by thomas on 02/10/2017.
 */
public class LayerWidget extends Composite {

    interface LayerWidgetUiBinder extends UiBinder<HTMLPanel, LayerWidget> {
    }

    private static LayerWidgetUiBinder ourUiBinder = GWT.create(LayerWidgetUiBinder.class);

    @UiField
    MaterialCheckBox layerSelection;

    public LayerWidget(DatasetAccessOGC datasetAccessOGC) {

        initWidget(ourUiBinder.createAndBindUi(this));

        layerSelection.setText(datasetAccessOGC.getTitle());

    }

    public HasValueChangeHandlers<Boolean> getSelection() {
        return layerSelection;
    }

    public void setLoading(boolean loading) {

    }

}