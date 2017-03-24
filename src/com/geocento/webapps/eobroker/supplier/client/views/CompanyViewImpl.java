package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.CountryEditor;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.shared.entities.COMPANY_SIZE;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.widgets.AwardWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.richeditor.MaterialRichEditor;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.html.Option;

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
    MaterialRichEditor fullDescription;
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
    CountryEditor country;
    @UiField
    MaterialListBox companySize;
    @UiField
    MaterialRow awards;
    @UiField
    MaterialLabel awardsMessage;
    @UiField
    MaterialTextArea address;
    @UiField
    MaterialButton addAward;
    @UiField
    MaterialLink viewClient;
    @UiField
    MaterialDatePicker startedIn;

    public CompanyViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        template.setPlace("companies");

        for(COMPANY_SIZE companySize : COMPANY_SIZE.values()) {
            this.companySize.add(new Option(companySize.toString()));
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
    public TemplateView getTemplateView() {
        return template;
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
    public void setAwards(List<String> awards) {
        this.awards.clear();
        if(awards != null && awards.size() == 0) {
            for(String award : awards) {
                addAward(award);
            }
        }
        updateAwardDisplay();
    }

    @UiHandler("addAward")
    void addAward(ClickEvent clickEvent) {
        addAward("");
        updateAwardDisplay();
    }

    private void addAward(String award) {
        final MaterialColumn materialColumn = new MaterialColumn(12, 12, 6);
        AwardWidget awardWidget = new AwardWidget();
        awardWidget.getText().setText(award);
        materialColumn.add(awardWidget);
        this.awards.add(materialColumn);
        awardWidget.getRemove().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CompanyViewImpl.this.awards.remove(materialColumn);
                updateAwardDisplay();
            }
        });
    }

    private void updateAwardDisplay() {
        boolean hasAwards = getAwards().size() > 0;
        awardsMessage.setVisible(!hasAwards);
        if(!hasAwards) {
            awardsMessage.setText("No awards, use the button below to add a new award");
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
    public List<String> getAwards() {
        List<String> awards = new ArrayList<String>();
        for(int index = 0; index < this.awards.getWidgetCount(); index++) {
            Widget widget = this.awards.getWidget(index);
            if(widget instanceof MaterialColumn) {
                MaterialColumn materialColumn = (MaterialColumn) widget;
                if(materialColumn.getWidgetCount() > 0) {
                    Widget awardWidget = materialColumn.getWidget(0);
                    if(awardWidget instanceof AwardWidget) {
                        awards.add(((AwardWidget) awardWidget).getText().getText());
                    }
                }
            }
        }
        return awards;
    }

    @Override
    public HasClickHandlers getViewClient() {
        return viewClient;
    }

}