package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.CountryEditor;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLoadingButton;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.COMPANY_SIZE;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.widgets.AffiliateWidget;
import com.geocento.webapps.eobroker.customer.client.widgets.CategorySearchBox;
import com.geocento.webapps.eobroker.customer.client.widgets.UserImageWidget;
import com.geocento.webapps.eobroker.customer.shared.UserDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.html.Option;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @UiField
    CountryEditor country;
    @UiField
    MaterialListBox companySize;
    @UiField
    MaterialRow affiliates;
    @UiField
    MaterialLabel affiliatesMessage;
    @UiField
    MaterialTextArea address;
    @UiField
    MaterialLoadingButton addAffiliate;
    @UiField
    MaterialLink viewClient;
    @UiField
    MaterialDatePicker startedIn;
    @UiField
    CategorySearchBox searchBox;
    @UiField
    MaterialPanel companyUsers;
    @UiField
    MaterialLoadingButton inviteColleagues;

    public CompanyViewImpl(ClientFactoryImpl clientFactory) {

        initWidget(ourUiBinder.createAndBindUi(this));

        for(COMPANY_SIZE companySize : COMPANY_SIZE.values()) {
            this.companySize.add(new Option(companySize.toString()));
        }

        inviteColleagues.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert("TODO - request emails and send an invitation");
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
        return imageUploader.getImageUrl();
    }

    @Override
    public void setIconUrl(String iconURL) {
        imageUploader.setImageUrl(iconURL);
    }

    @Override
    public Date getStartedIn() {
        return startedIn.getDate();
    }

    @Override
    public void setStartedIn(Date date) {
        startedIn.setDate(date);
    }

    @Override
    public HasText getAddress() {
        return address;
    }

    @Override
    public void setCountryCode(String countryCode) {
        country.selectCountry(countryCode);
    }

    @Override
    public void setCompanySize(COMPANY_SIZE companySize) {
        this.companySize.setSelectedValue(companySize == null ? COMPANY_SIZE.small.toString() : companySize.toString());
    }

    @Override
    public void setAffiliates(List<CompanyDTO> affiliates) {
        this.affiliates.clear();
        if(affiliates != null && affiliates.size() > 0) {
            for(CompanyDTO affiliate : affiliates) {
                addAffiliate(affiliate);
            }
        }
        updateAffiliateDisplay();
    }

    @UiHandler("addAffiliate")
    void addAffiliate(ClickEvent clickEvent) {
        Suggestion suggestion = (Suggestion) searchBox.getSelectedObject().getO();
        if(suggestion == null) {
            Window.alert("Please select a company first");
        }
        // check company hasn't been added already
        Long companyID = Long.parseLong(suggestion.getUri().split("::")[1]);
        boolean included = ListUtil.findValue(getAffiliates(), new ListUtil.CheckValue<CompanyDTO>() {
            @Override
            public boolean isValue(CompanyDTO value) {
                return value.getId().equals(companyID);
            }
        }) != null;
        if(included) {
            Window.alert("Company already included");
        }
        // now load the company details
        try {
            addAffiliate.setLoading(true);
            REST.withCallback(new MethodCallback<CompanyDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    addAffiliate.setLoading(false);
                    Window.alert(method.getResponse().getText());
                }

                @Override
                public void onSuccess(Method method, CompanyDTO companyDTO) {
                    addAffiliate.setLoading(false);
                    addAffiliate(companyDTO);
                    updateAffiliateDisplay();
                }
            }).call(ServicesUtil.assetsService).getCompany(companyID);
        } catch (Exception e) {
        }
    }

    private void addAffiliate(CompanyDTO companyDTO) {
        final MaterialColumn materialColumn = new MaterialColumn(12, 6, 4);
        AffiliateWidget affiliateWidget = new AffiliateWidget(companyDTO);
        materialColumn.add(affiliateWidget);
        this.affiliates.add(materialColumn);
        affiliateWidget.getRemove().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CompanyViewImpl.this.affiliates.remove(materialColumn);
                updateAffiliateDisplay();
            }
        });
    }

    private void updateAffiliateDisplay() {
        boolean hasAffiliates = getAffiliates().size() > 0;
        affiliatesMessage.setVisible(!hasAffiliates);
        if(!hasAffiliates) {
            affiliatesMessage.setText("No affiliates, use the button below to add a new affiliate");
        }
    }

    @Override
    public String getCountryCode() {
        return country.getCountry();
    }

    @Override
    public COMPANY_SIZE getCompanySize() {
        return COMPANY_SIZE.valueOf(companySize.getSelectedValue());
    }

    @Override
    public List<CompanyDTO> getAffiliates() {
        List<CompanyDTO> affiliates = new ArrayList<>();
        for(int index = 0; index < this.affiliates.getWidgetCount(); index++) {
            Widget widget = this.affiliates.getWidget(index);
            if(widget instanceof MaterialColumn) {
                MaterialColumn materialColumn = (MaterialColumn) widget;
                if(materialColumn.getWidgetCount() > 0) {
                    Widget affiliateWidget = materialColumn.getWidget(0);
                    if(affiliateWidget instanceof AffiliateWidget) {
                        affiliates.add(((AffiliateWidget) affiliateWidget).getCompanyDTO());
                    }
                }
            }
        }
        return affiliates;
    }

    @Override
    public void setUsers(List<UserDTO> users) {
        companyUsers.clear();
        if(users == null || users.size() == 0) {
            companyUsers.add(new MaterialLabel("You are the only user from this company on the EO Broker. Click on the button below to invite colleagues."));
        } else {
            MaterialRow materialRow = new MaterialRow();
            companyUsers.add(materialRow);
            for(UserDTO userDTO : users) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 4);
                materialColumn.add(new UserImageWidget(userDTO));
                materialRow.add(materialColumn);
            }
        }
    }

    @Override
    public HasClickHandlers getViewClient() {
        return viewClient;
    }

    @Override
    public void setEditable(boolean editable) {
        name.setEnabled(editable);
        email.setEnabled(editable);
        description.setEnabled(editable);
        fullDescription.setEnabled(editable);
        imageUploader.setEnabled(editable);
        website.setEnabled(editable);
        title.setEnabled(editable);
        country.setEnabled(editable);
        companySize.setEnabled(editable);
        address.setEnabled(editable);
        startedIn.setEnabled(editable);
        companyUsers.setEnabled(editable);
    }

}