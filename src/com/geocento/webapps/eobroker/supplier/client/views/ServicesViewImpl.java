package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.fileuploader.MaterialFileUploader;
import gwt.material.design.addins.client.fileuploader.base.UploadFile;
import gwt.material.design.addins.client.fileuploader.events.DragOverEvent;
import gwt.material.design.addins.client.fileuploader.events.SuccessEvent;
import gwt.material.design.addins.client.fileuploader.events.TotalUploadProgressEvent;
import gwt.material.design.addins.client.richeditor.MaterialRichEditor;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.animate.MaterialAnimator;
import gwt.material.design.client.ui.animate.Transition;

/**
 * Created by thomas on 09/05/2016.
 */
public class ServicesViewImpl extends Composite implements ServicesView {

    private Presenter presenter;

    interface ServicesViewUiBinder extends UiBinder<Widget, ServicesViewImpl> {
    }

    private static ServicesViewUiBinder ourUiBinder = GWT.create(ServicesViewUiBinder.class);
    @UiField
    MaterialTitle title;
    @UiField
    MaterialTextBox name;
    @UiField
    MaterialTextBox email;
    @UiField
    MaterialTextBox website;
    @UiField
    MaterialFileUploader imageUploader;
    @UiField
    MaterialImage iconPreview;
    @UiField
    MaterialLabel iconName;
    @UiField
    MaterialLabel iconSize;
    @UiField
    MaterialProgress iconProgress;
    @UiField
    MaterialTextArea description;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialRichEditor fullDescription;
    @UiField
    MaterialImage logo;

    public ServicesViewImpl(ClientFactoryImpl clientFactory) {

        initWidget(ourUiBinder.createAndBindUi(this));

        final String uploadedUrl = "/upload/image/";
        imageUploader.setUrl(uploadedUrl);
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
                iconPreview.setUrl(event.getResponse().getMessage().replaceAll("<value>", "").replaceAll("</value>", ""));
            }
        });

        imageUploader.addDragOverHandler(new DragOverEvent.DragOverHandler() {
            @Override
            public void onDragOver(DragOverEvent event) {
                MaterialAnimator.animate(Transition.RUBBERBAND, imageUploader, 0);
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void setTitleLine(String title) {
        this.title.setTitle(title);
    }

    @Override
    public HasText getName() {
        return name;
    }

    @Override
    public HasText getEmail() {
        return email;
    }

    @Override
    public HasText getWebsite() {
        return website;
    }

    @Override
    public HasText getDescription() {
        return description;
    }

    @Override
    public String getFullDescription() {
        return fullDescription.getHTML();
    }

    @Override
    public void setFullDescription(String fullDescription) {
        this.fullDescription.setHTML(fullDescription);
    }

    @Override
    public HasClickHandlers getSubmit() {
        return submit;
    }

    @Override
    public String getIconUrl() {
        return iconPreview.getUrl();
    }

    @Override
    public void setIconUrl(String iconURL) {
        iconPreview.setUrl(iconURL);
    }

    @Override
    public HasClickHandlers getHomeButton() {
        return logo;
    }

}