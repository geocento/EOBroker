package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.ExpandPanel;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
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
    protected MaterialTextBox uri;
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
    MaterialIntegerBox size;
    @UiField
    MaterialImageUploader imageUpload;

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
        String uriName = "Unknown";
        if(datasetAccess instanceof DatasetAccessFile) {
            iconType = IconType.GET_APP;
            text = "File";
            uriName = "The File Download URL";
        } else if(datasetAccess instanceof DatasetAccessAPP) {
            iconType = IconType.WEB;
            text = "Application";
            uriName = "The URL to access the web application";
        } else if(datasetAccess instanceof DatasetAccessOGC) {
            iconType = IconType.LAYERS;
            text = "OGC services";
            uriName = "The WMS layer name for this data";
        } else if(datasetAccess instanceof DatasetAccessAPI) {
            iconType = IconType.CLOUD_CIRCLE;
            text = "API";
            uriName = "The URL to the API documentation";
        }
        typeTooltip.setText(text);
        type.setIconType(iconType);
        boolean newDataAccess = datasetAccess.getTitle() == null;
        panel.setLabel(newDataAccess ? "New data access - TODO fill information" : datasetAccess.getTitle());
        imageUpload.setImageUrl(datasetAccess.getImageUrl());
        title.setText(datasetAccess.getTitle());
        pitch.setText(datasetAccess.getPitch());
        uri.setText(datasetAccess.getUri());
        uri.setReadOnly(!editableUri);
        uri.setPlaceholder(uriName);
        boolean hasFile = !(datasetAccess instanceof DatasetAccessAPI || datasetAccess instanceof DatasetAccessAPP);
        size.setVisible(hasFile);
        if(hasFile) {
            uri.setPlaceholder(datasetAccess instanceof DatasetAccessFile ? "The link to download the sample file" : "The link to download the original file for the data, leave empty if not provided");
            size.setPlaceholder(datasetAccess instanceof DatasetAccessFile ? "The file size in bytes, leave empty if unknown" : "The sample file size in bytes, leave empty if not provided or unknown");
            if(datasetAccess.getSize() != null) {
                size.setValue(Integer.valueOf(datasetAccess.getSize()));
            }
        }
        panel.setOpen(newDataAccess);
    }

    public DatasetAccess getDatasetAccess() {
        // update values first
        datasetAccess.setTitle(title.getText());
        datasetAccess.setPitch(pitch.getText());
        // required to be able to leave the size empty
        try {
            datasetAccess.setSize(size.getValue());
        } catch (Exception e) {
            datasetAccess.setSize(null);
        }
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

    public HasClickHandlers getAccessLink() {
        return type;
    }

}