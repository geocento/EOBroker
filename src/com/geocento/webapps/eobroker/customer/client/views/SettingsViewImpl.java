package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.places.CompanyPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;

/**
 * Created by thomas on 09/05/2016.
 */
public class SettingsViewImpl extends Composite implements SettingsView {

    interface DummyUiBinder extends UiBinder<Widget, SettingsViewImpl> {
    }

    private static DummyUiBinder ourUiBinder = GWT.create(DummyUiBinder.class);

    @UiField
    MaterialLink editCompany;
    @UiField
    MaterialTextBox fullName;
    @UiField
    MaterialTextBox email;
    @UiField
    MaterialImageUploader iconUrl;
    @UiField
    MaterialLabel companyName;
    @UiField
    MaterialImageLoading companyImage;
    @UiField
    MaterialLabel companyDescription;
    @UiField
    MaterialButton submit;

    private Presenter presenter;

    public SettingsViewImpl(ClientFactoryImpl clientFactory) {

        initWidget(ourUiBinder.createAndBindUi(this));

        editCompany.setHref("#" + PlaceHistoryHelper.convertPlace(new CompanyPlace()));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HasText getFullName() {
        return fullName;
    }

    @Override
    public void setIconUrl(String userIconUrl) {
        iconUrl.setImageUrl(userIconUrl);
    }

    @Override
    public String getIconUrl() {
        return iconUrl.getImageUrl();
    }

    @Override
    public HasText getEmail() {
        return email;
    }

    @Override
    public void setCompany(CompanyDTO companyDTO) {
        companyImage.setImageUrl(companyDTO.getIconURL());
        companyName.setText(companyDTO.getName());
        companyDescription.setText(companyDTO.getDescription());
        // cannot edit if supplier needs to go to the supplier application instead
        //editCompany.setVisible(!companyDTO.isSupplier());
    }

    @Override
    public HasClickHandlers getSaveButton() {
        return submit;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}