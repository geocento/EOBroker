package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

/**
 * Created by thomas on 08/11/2016.
 */
public class DataAccessWidget extends Composite {

    interface DataAccessWidgetUiBinder extends UiBinder<Widget, DataAccessWidget> {
    }

    private static DataAccessWidgetUiBinder ourUiBinder = GWT.create(DataAccessWidgetUiBinder.class);

    @UiField
    MaterialTextBox pitch;
    @UiField
    MaterialLink type;
    @UiField
    MaterialTextBox uri;
    @UiField
    MaterialTextBox title;
    @UiField
    MaterialRow fields;
    @UiField
    MaterialButton remove;

    protected final DatasetAccess datasetAccess;

    protected boolean editableUri;

    public DataAccessWidget(DatasetAccess datasetAccess) {
        this(datasetAccess, false);
    }

    public DataAccessWidget(DatasetAccess datasetAccess, boolean editableUri) {

        this.datasetAccess = datasetAccess;
        this.editableUri = editableUri;

        initWidget(ourUiBinder.createAndBindUi(this));

        String text = "Unknown";
        IconType iconType = null;
        if(datasetAccess instanceof DatasetAccessFile) {
            iconType = IconType.GET_APP;
            text = "File";
        } else if(datasetAccess instanceof DatasetAccessAPP) {
            iconType = IconType.WEB;
            text = "Application";
        } else if(datasetAccess instanceof DatasetAccessOGC) {
            iconType = IconType.LAYERS;
            text = "OGC services";
        } else if(datasetAccess instanceof DatasetAccessAPI) {
            iconType = IconType.CLOUD_CIRCLE;
            text = "API";
        }
        type.setText(text);
        type.setIconType(iconType);
        title.setText(datasetAccess.getTitle());
        pitch.setText(datasetAccess.getPitch());
        uri.setText(datasetAccess.getUri());
        uri.setReadOnly(!editableUri);
    }

    public DatasetAccess getDatasetAccess() {
        // update values first
        datasetAccess.setTitle(title.getText());
        datasetAccess.setPitch(pitch.getText());
        datasetAccess.setUri(uri.getText());
        return datasetAccess;
    }

    protected void addField(MaterialWidget panel) {
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        fields.add(materialColumn);
        materialColumn.add(panel);
    }

    public HasClickHandlers getRemove() {
        return remove;
    }

}