package com.geocento.webapps.eobroker.common.client.widgets.maps;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import gwt.material.design.addins.client.fileuploader.MaterialFileUploader;
import gwt.material.design.addins.client.fileuploader.base.UploadFile;
import gwt.material.design.addins.client.fileuploader.events.*;
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

    public static interface Presenter {
        void aoiSelected(AoIDTO aoIDTO);
    }

    @UiField
    MaterialFileUploader uploadAoI;
    @UiField
    MaterialLabel iconName;
    @UiField
    MaterialLabel iconSize;
    @UiField
    MaterialProgress iconProgress;

    private final MaterialModal materialModal;

    private static UploadAoI instance = null;

    private Presenter presenter;

    public UploadAoI() {

        materialModal = ourUiBinder.createAndBindUi(this);

        final String uploadUrl = GWT.getHostPageBaseURL() + "upload/geometry/";
        uploadAoI.setUrl(uploadUrl);

        uploadAoI.addAddedFileHandler(new AddedFileEvent.AddedFileHandler<UploadFile>() {
            @Override
            public void onAddedFile(AddedFileEvent<UploadFile> event) {
                String fileName = event.getTarget().getName();
                if(!fileName.endsWith(".kml")) {
                    Window.alert("Sorry on KML files allowed for now");
                    uploadAoI.fireErrorEvent(fileName, event.getTarget().getLastModified() + "", event.getTarget().getFileSize() + "", event.getTarget().getType(), "500", "", "Only KML files are allowed");
                }
            }
        });
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
                String response = StringUtils.extract(event.getResponse().getBody(), "<value>", "</value>");
                AoIDTO aoIDTO = new AoIDTO();
                JSONObject aoiJson = JSONParser.parseLenient(response).isObject();
                aoIDTO.setName(aoiJson.get("name").isString().stringValue());
                aoIDTO.setWktGeometry(aoiJson.get("wktGeometry").isString().stringValue());
                presenter.aoiSelected(aoIDTO);
                hide();
            }
        });

        uploadAoI.addErrorHandler(new ErrorEvent.ErrorHandler<UploadFile>() {
            @Override
            public void onError(ErrorEvent<UploadFile> event) {
                Window.alert("Error loading file"); //, message is " + event.getResponse().getMessage());
            }
        });

        uploadAoI.addDragOverHandler(new DragOverEvent.DragOverHandler() {
            @Override
            public void onDragOver(DragOverEvent event) {
                MaterialAnimator.animate(Transition.RUBBERBAND, uploadAoI, 0);
            }
        });

        // add to document
        RootPanel.get().add(materialModal);

    }

    public static UploadAoI getInstance() {
        if(instance == null) {
            instance = new UploadAoI();
        }
        return instance;
    }

    // TODO - clean the dropzone?
    public void display(final Presenter presenter) {
        this.presenter = presenter;
        materialModal.openModal();
    }

    private void hide() {
        materialModal.closeModal();
    }

}