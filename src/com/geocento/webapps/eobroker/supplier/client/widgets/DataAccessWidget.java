package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialFileUploader;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.places.VisualisationPlace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.fileuploader.base.UploadFile;
import gwt.material.design.addins.client.fileuploader.events.DragOverEvent;
import gwt.material.design.addins.client.fileuploader.events.SuccessEvent;
import gwt.material.design.addins.client.fileuploader.events.TotalUploadProgressEvent;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialListBox;
import gwt.material.design.client.ui.MaterialTextBox;
import gwt.material.design.client.ui.animate.MaterialAnimator;
import gwt.material.design.client.ui.animate.Transition;
import gwt.material.design.client.ui.html.Option;

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

    private final DatasetAccess datasetAccess;

    public DataAccessWidget(DatasetAccess datasetAccess) {
        this(datasetAccess, false);
    }

    public DataAccessWidget(DatasetAccess datasetAccess, boolean sample) {

        this.datasetAccess = datasetAccess;

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
        pitch.setText(datasetAccess.getPitch());
        uri.setText(datasetAccess.getUri());
        uri.setReadOnly(sample);
    }

    public DatasetAccess getDatasetAccess() {
        // update values first
        datasetAccess.setPitch(pitch.getText());
        datasetAccess.setUri(uri.getText());
        return datasetAccess;
    }
}