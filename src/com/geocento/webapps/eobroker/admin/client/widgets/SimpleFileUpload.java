package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialLoadingButton;
import com.geocento.webapps.eobroker.common.client.widgets.material.MaterialFileUploader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.fileuploader.base.UploadFile;
import gwt.material.design.addins.client.fileuploader.events.SuccessEvent;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by thomas on 29/11/2017.
 */
public class SimpleFileUpload extends MaterialPanel {

    interface SimpleFileUploadUiBinder extends UiBinder<Widget, SimpleFileUpload> {
    }

    private static SimpleFileUploadUiBinder ourUiBinder = GWT.create(SimpleFileUploadUiBinder.class);

    @UiField
    MaterialLabel uploadTitle;
    @UiField
    MaterialLoadingButton uploadButton;
    @UiField
    MaterialFileUploader fileImport;

    public SimpleFileUpload() {

        add(ourUiBinder.createAndBindUi(this));

        fileImport.addAddedFileHandler(event -> {
            uploadButton.setLoading(true);
        });
        fileImport.addErrorHandler(event -> {
            uploadButton.setLoading(false);
        });
        fileImport.addCancelHandler(event -> {
            uploadButton.setLoading(false);
        });
        fileImport.addSuccessHandler(event -> {
            uploadButton.setLoading(false);
        });

    }

    public HandlerRegistration addSuccessHandler(final SuccessEvent.SuccessHandler<UploadFile> handler) {
        return fileImport.addSuccessHandler(handler);
    }

    public void setText(String text) {
        uploadTitle.setText(text);
    }

    public void setUrl(String url) {
        fileImport.setUrl(url);
    }

}