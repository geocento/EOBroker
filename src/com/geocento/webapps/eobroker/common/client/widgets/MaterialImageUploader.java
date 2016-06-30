package com.geocento.webapps.eobroker.common.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.fileuploader.base.UploadFile;
import gwt.material.design.addins.client.fileuploader.events.DragOverEvent;
import gwt.material.design.addins.client.fileuploader.events.SuccessEvent;
import gwt.material.design.addins.client.fileuploader.events.TotalUploadProgressEvent;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialProgress;
import gwt.material.design.client.ui.animate.MaterialAnimator;
import gwt.material.design.client.ui.animate.Transition;

/**
 * Created by thomas on 22/06/2016.
 */
public class MaterialImageUploader extends Composite {

    interface MaterialImageUploaderUiBinder extends UiBinder<Widget, MaterialImageUploader> {
    }

    private static MaterialImageUploaderUiBinder ourUiBinder = GWT.create(MaterialImageUploaderUiBinder.class);

    @UiField
    MaterialImage iconPreview;
    @UiField
    MaterialLabel iconName;
    @UiField
    MaterialLabel iconSize;
    @UiField
    MaterialProgress iconProgress;
    @UiField
    MaterialFileUploader imageUploader;

    public MaterialImageUploader() {
        initWidget(ourUiBinder.createAndBindUi(this));

        final String uploadUrl = "/upload/image/";
        imageUploader.setUrl(uploadUrl);
        // Added the progress to card uploader
        imageUploader.addTotalUploadProgressHandler(new TotalUploadProgressEvent.TotalUploadProgressHandler() {
            @Override
            public void onTotalUploadProgress(TotalUploadProgressEvent event) {
                iconProgress.setPercent(event.getProgress());
            }
        });

        imageUploader.addSuccessHandler(new SuccessEvent.SuccessHandler<UploadFile>() {
            @Override
            public void onSuccess(SuccessEvent<UploadFile> event) {
                iconName.setText(event.getTarget().getName());
                iconSize.setText(event.getTarget().getType());
                iconPreview.setUrl(StringUtils.extract(event.getResponse().getMessage(), "<value>", "</value>"));
            }
        });

        imageUploader.addDragOverHandler(new DragOverEvent.DragOverHandler() {
            @Override
            public void onDragOver(DragOverEvent event) {
                MaterialAnimator.animate(Transition.RUBBERBAND, imageUploader, 0);
            }
        });
    }

    public void setImageUrl(String imageUrl) {
        if(imageUrl != null) {
            iconPreview.setUrl(imageUrl);
        } else {
            iconPreview.clear();
        }
    }

    public String getImageUrl() {
        return iconPreview.getUrl();
    }

    public void setImageWidth(int width) {
        imageUploader.setParameter("width", width + "");
        iconPreview.setWidth(width + "px");
    }

    public void setImageHeight(int height) {
        imageUploader.setParameter("height", height + "");
        iconPreview.setHeight(height + "px");
    }
}