package com.geocento.webapps.eobroker.common.client.widgets.maps;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialFileUploader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RootPanel;
import gwt.material.design.addins.client.fileuploader.base.UploadFile;
import gwt.material.design.addins.client.fileuploader.events.DragOverEvent;
import gwt.material.design.addins.client.fileuploader.events.SuccessEvent;
import gwt.material.design.addins.client.fileuploader.events.TotalUploadProgressEvent;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialModal;
import gwt.material.design.client.ui.MaterialProgress;
import gwt.material.design.client.ui.animate.MaterialAnimator;
import gwt.material.design.client.ui.animate.Transition;

/**
 * Created by thomas on 15/11/2016.
 */
public class UploadAoI {

    interface UploadAoIUiBinder extends UiBinder<MaterialModal, UploadAoI> {
    }

    private static UploadAoIUiBinder ourUiBinder = GWT.create(UploadAoIUiBinder.class);

    private final MaterialModal materialModal;
    @UiField
    MaterialFileUploader uploadAoI;
    @UiField
    MaterialLabel iconName;
    @UiField
    MaterialLabel iconSize;
    @UiField
    MaterialProgress iconProgress;

    private static UploadAoI instance = null;

    public UploadAoI() {

        materialModal = ourUiBinder.createAndBindUi(this);

        // add to document
        RootPanel.get().add(materialModal);

        final String uploadUrl = GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/", "") + "upload/image/";
        uploadAoI.setUrl(uploadUrl);
        // Added the progress to card uploader
        uploadAoI.addTotalUploadProgressHandler(new TotalUploadProgressEvent.TotalUploadProgressHandler() {
            @Override
            public void onTotalUploadProgress(TotalUploadProgressEvent event) {
                iconProgress.setPercent(event.getProgress());
            }
        });

        uploadAoI.addSuccessHandler(new SuccessEvent.SuccessHandler<UploadFile>() {
            @Override
            public void onSuccess(SuccessEvent<UploadFile> event) {
                iconName.setText(event.getTarget().getName());
                iconSize.setText(event.getTarget().getType());
            }
        });

        uploadAoI.addDragOverHandler(new DragOverEvent.DragOverHandler() {
            @Override
            public void onDragOver(DragOverEvent event) {
                MaterialAnimator.animate(Transition.RUBBERBAND, uploadAoI, 0);
            }
        });
    }

    public static UploadAoI getInstance() {
        if(instance == null) {
            instance = new UploadAoI();
        }
        return instance;
    }

    public void display() {
        materialModal.openModal();
    }

}