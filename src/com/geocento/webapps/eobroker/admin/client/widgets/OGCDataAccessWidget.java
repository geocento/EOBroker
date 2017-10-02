package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.ExpandPanel;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.supplier.client.widgets.StyleNameEditor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.constants.IconPosition;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

/**
 * Created by thomas on 08/11/2016.
 */
public class OGCDataAccessWidget extends Composite {

    interface DataAccessWidgetUiBinder extends UiBinder<Widget, OGCDataAccessWidget> {
    }

    private static DataAccessWidgetUiBinder ourUiBinder = GWT.create(DataAccessWidgetUiBinder.class);

    @UiField
    MaterialTextBox pitch;
    @UiField
    MaterialLink type;
    @UiField
    MaterialTextBox title;
    @UiField
    MaterialRow fields;
    @UiField
    MaterialButton remove;
    @UiField
    ExpandPanel panel;
    @UiField
    MaterialTooltip typeTooltip;
    @UiField
    MaterialCheckBox corsEnabled;
    @UiField
    MaterialTextBox layerName;
    @UiField
    MaterialTextBox serverUrl;

    protected final DatasetAccessOGC datasetAccess;

    public OGCDataAccessWidget(DatasetAccessOGC datasetAccess) {

        this.datasetAccess = datasetAccess;

        initWidget(ourUiBinder.createAndBindUi(this));

        typeTooltip.setText("OGC services");
        type.setIconType(IconType.LAYERS);
        boolean newDataAccess = datasetAccess.getTitle() == null;
        panel.setLabel(newDataAccess ? "New data access - TODO fill information" : datasetAccess.getTitle());
        title.setText(datasetAccess.getTitle());
        pitch.setText(datasetAccess.getPitch());
        setServerUrl(datasetAccess.getServerUrl());
        setLayerName(datasetAccess.getLayerName());

        panel.setOpen(newDataAccess);
        corsEnabled.setValue(datasetAccess.isCorsEnabled());
    }

    private void setServerUrl(String serverUrl) {
        this.serverUrl.setText(serverUrl);
    }

    private void setLayerName(String layerName) {
        this.layerName.setText(layerName);
    }

    public DatasetAccessOGC getDatasetAccess() {
        datasetAccess.setTitle(title.getText());
        datasetAccess.setPitch(pitch.getText());
        datasetAccess.setServerUrl(serverUrl.getText());
        datasetAccess.setLayerName(layerName.getText());
        datasetAccess.setCorsEnabled(corsEnabled.getValue());
        return datasetAccess;
    }

    public HasClickHandlers getRemove() {
        return remove;
    }

    public HasClickHandlers getAccessLink() {
        return type;
    }

}