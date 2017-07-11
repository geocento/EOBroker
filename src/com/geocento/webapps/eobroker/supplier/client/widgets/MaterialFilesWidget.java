package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialFileUploader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialRow;

/**
 * Created by thomas on 11/07/2017.
 */
public class MaterialFilesWidget extends Composite {

    interface MaterialFilesWidgetUiBinder extends UiBinder<Widget, MaterialFilesWidget> {
    }

    private static MaterialFilesWidgetUiBinder ourUiBinder = GWT.create(MaterialFilesWidgetUiBinder.class);
    @UiField
    MaterialFileUploader uploader;
    @UiField
    MaterialRow listOfFiles;

    public MaterialFilesWidget() {

        initWidget(ourUiBinder.createAndBindUi(this));

        // set dummy URL for now
        uploader.setUrl("");
        uploader.addSuccessHandler(event -> {

        });
    }

    public void setUploadUrl(String url) {
        uploader.setUrl(url);
    }

    public void setMaxFileSize(int maxFileSize) {
        uploader.setMaxFileSize(maxFileSize);
    }

    public void setAcceptedFiles(String acceptedFiles) {
        uploader.setAcceptedFiles(acceptedFiles);
    }

}