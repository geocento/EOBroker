package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.CountryEditor;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLoadingButton;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialMessage;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.styles.StyleResources;
import com.geocento.webapps.eobroker.customer.client.widgets.CategorySearchBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.ImageType;
import gwt.material.design.client.ui.*;

/**
 * Created by thomas on 09/05/2016.
 */
public class RequestAccessViewImpl extends Composite implements RequestAccessView {

    interface RequestViewUiBinder extends UiBinder<Widget, RequestAccessViewImpl> {
    }

    private static RequestViewUiBinder ourUiBinder = GWT.create(RequestViewUiBinder.class);

    @UiField(provided = true)
    MaterialImage logo;
    @UiField
    MaterialTextBox userName;
    @UiField
    MaterialTextBox password;
    @UiField
    MaterialLoadingButton submit;
    @UiField
    MaterialTextBox fullName;
    @UiField
    MaterialTextBox email;
    @UiField
    MaterialTextBox phoneNumber;
    @UiField
    CategorySearchBox company;
    @UiField
    CountryEditor companyCountry;
    @UiField
    MaterialTextBox companyAddress;
    @UiField
    MaterialTextBox companyDescription;
    @UiField
    MaterialTextBox companyName;
    @UiField
    MaterialPanel companyRegistration;
    @UiField
    MaterialCheckBox supplierCompany;
    @UiField
    MaterialMessage message;
    @UiField
    MaterialPanel content;

    private Presenter presenter;

    private CompanyDTO companyDTO;

    public RequestAccessViewImpl(ClientFactoryImpl clientFactory) {

        logo = new MaterialImage(StyleResources.INSTANCE.logoEOBroker(), ImageType.CIRCLE);

        initWidget(ourUiBinder.createAndBindUi(this));

        company.setPresenter(suggestion -> {
            // create shallow company
            boolean changed = false;
            if(suggestion == null) {
                changed = companyDTO != null;
                companyDTO = null;
            } else {
                CompanyDTO companyDTO = new CompanyDTO();
                companyDTO.setName(suggestion.getName());
                companyDTO.setId(Long.parseLong(suggestion.getUri().replace("companies::", "")));
                changed = RequestAccessViewImpl.this.companyDTO == null || !RequestAccessViewImpl.this.companyDTO.getId().equals(companyDTO.getId());
                RequestAccessViewImpl.this.companyDTO = companyDTO;
                displayCompanyRegistration(false);
            }
        });

        displayCompanyRegistration(false);
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
    public HasText getUserName() {
        return userName;
    }

    @Override
    public HasText getPassword() {
        return password;
    }

    @Override
    public HasKeyPressHandlers getPasswordBox() {
        return password;
    }

    @Override
    public HasText getFullName() {
        return fullName;
    }

    @Override
    public HasText getEmail() {
        return email;
    }

    @Override
    public HasText getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public CompanyDTO getCompany() {
        return companyDTO;
    }

    @Override
    public HasText getCompanyName() {
        return companyName;
    }

    @Override
    public HasText getCompanyDescription() {
        return companyDescription;
    }

    @Override
    public HasText getCompanyAddress() {
        return companyAddress;
    }

    @Override
    public String getCompanyCountryCode() {
        return companyCountry.getCountry();
    }

    @Override
    public HasValue<Boolean> isSupplier() {
        return supplierCompany;
    }

    private void displayCompanyRegistration(boolean display) {
        companyRegistration.setVisible(display);
    }
    
    @UiHandler("createNewCompany")
    void createNewCompany(ClickEvent clickEvent) {
        displayCompanyRegistration(true);
    }

    @Override
    public HasClickHandlers getSubmit() {
        return submit;
    }

    @Override
    public void displayCompanyCreationError(String message) {
        this.message.setVisible(true);
        this.message.displayErrorMessage(message);
    }

    @Override
    public void displayUserCreationError(String message) {
        this.message.setVisible(true);
        this.message.displayErrorMessage(message);
    }

    @Override
    public void displayCreationSuccess(String message) {
        content.clear();
        MaterialMessage materialMessage = new MaterialMessage();
        materialMessage.displaySuccessMessage(message);
        materialMessage.setFontSize("1.2em");
        content.add(materialMessage);
    }

    @Override
    public void displayLoading(String message) {
        submit.setLoading(true);
        MaterialToast.fireToast(message);
    }

    @Override
    public void hideLoading() {
        submit.setLoading(false);
    }

}