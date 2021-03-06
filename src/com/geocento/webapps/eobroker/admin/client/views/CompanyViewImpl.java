package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.shared.entities.REGISTRATION_STATUS;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;

/**
 * Created by thomas on 09/05/2016.
 */
public class CompanyViewImpl extends Composite implements CompanyView {

    private Presenter presenter;

    interface CompanyViewUiBinder extends UiBinder<Widget, CompanyViewImpl> {
    }

    private static CompanyViewUiBinder ourUiBinder = GWT.create(CompanyViewUiBinder.class);
    @UiField
    MaterialTextBox name;
    @UiField
    MaterialTextBox email;
    @UiField
    MaterialTextArea description;
    @UiField
    com.geocento.webapps.eobroker.common.client.widgets.material.MaterialRichEditor fullDescription;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialImageUploader imageUploader;
    @UiField
    MaterialTextBox website;
    @UiField
    MaterialTitle title;
    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialButton validate;
    @UiField
    MaterialSwitch supplier;
    @UiField
    MaterialListValueBox<REGISTRATION_STATUS> status;

    public CompanyViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        for(REGISTRATION_STATUS status : REGISTRATION_STATUS.values()) {
            this.status.addItem(status);
        }
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
    public HasValue<Boolean> getSupplier() {
        return supplier;
    }

    @Override
    public HasValue<REGISTRATION_STATUS> getStatus() {
        return status;
    }

    @Override
    public HasText getEmail() {
        return email;
    }

    @Override
    public void displayValidate(boolean display) {
        validate.setVisible(false); //display);
    }

    @Override
    public HasClickHandlers getValidate() {
        return validate;
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
        return imageUploader.getImageUrl();
    }

    @Override
    public void setIconUrl(String iconURL) {
        imageUploader.setImageUrl(iconURL);
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void setLoading(String message) {
        template.setLoading(message);
    }

    @Override
    public void setLoadingError(String message) {
        template.setLoading(message);
    }

    @Override
    public void hideLoading(String message) {
        template.hideLoading(message);
    }

}