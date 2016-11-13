package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialFileUploader;
import com.geocento.webapps.eobroker.common.shared.entities.AccessType;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.fileuploader.base.UploadFile;
import gwt.material.design.addins.client.fileuploader.events.DragOverEvent;
import gwt.material.design.addins.client.fileuploader.events.SuccessEvent;
import gwt.material.design.addins.client.fileuploader.events.TotalUploadProgressEvent;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialListBox;
import gwt.material.design.client.ui.MaterialTextBox;
import gwt.material.design.client.ui.animate.MaterialAnimator;
import gwt.material.design.client.ui.animate.Transition;

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
    MaterialListBox type;
    @UiField
    MaterialTextBox uri;
    @UiField
    MaterialButton upload;
    @UiField
    MaterialFileUploader dataUploader;

    private final DatasetAccess datasetAccess;

    public DataAccessWidget(DatasetAccess datasetAccess) {

        this.datasetAccess = datasetAccess;

        initWidget(ourUiBinder.createAndBindUi(this));

        for(AccessType accessType : AccessType.values()) {
            type.addItem(accessType.getName(), accessType.toString());
        }

        pitch.setText(datasetAccess.getPitch());
        uri.setText(datasetAccess.getUri());

        final String uploadUrl = GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/", "") + "upload/datasets/";
        dataUploader.setUrl(uploadUrl);
        // Added the progress to card uploader
        dataUploader.addTotalUploadProgressHandler(new TotalUploadProgressEvent.TotalUploadProgressHandler() {
            @Override
            public void onTotalUploadProgress(TotalUploadProgressEvent event) {
/*
                iconProgress.setPercent(event.getProgress());
*/
            }
        });

        dataUploader.addSuccessHandler(new SuccessEvent.SuccessHandler<UploadFile>() {
            @Override
            public void onSuccess(SuccessEvent<UploadFile> event) {
                uri.setText(StringUtils.extract(event.getResponse().getMessage(), "<value>", "</value>"));
            }
        });

        dataUploader.addDragOverHandler(new DragOverEvent.DragOverHandler() {
            @Override
            public void onDragOver(DragOverEvent event) {
                MaterialAnimator.animate(Transition.RUBBERBAND, dataUploader, 0);
            }
        });
    }

    public DatasetAccess getDatasetAccess() {
        // update values first
        datasetAccess.setAccessType(AccessType.valueOf(type.getSelectedValue()));
        datasetAccess.setPitch(pitch.getText());
        datasetAccess.setUri(uri.getText());
        return datasetAccess;
    }
}