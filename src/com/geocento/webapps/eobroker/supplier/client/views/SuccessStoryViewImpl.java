package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.client.widgets.WidgetUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.widgets.CompanyRoleWidget;
import com.geocento.webapps.eobroker.supplier.client.widgets.CompanyTextBox;
import com.geocento.webapps.eobroker.supplier.shared.dtos.CompanyRoleDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class SuccessStoryViewImpl extends Composite implements SuccessStoryView {

    private Presenter presenter;

    interface ProjectViewUiBinder extends UiBinder<Widget, SuccessStoryViewImpl> {
    }

    private static ProjectViewUiBinder ourUiBinder = GWT.create(ProjectViewUiBinder.class);

    @UiField
    MaterialTextBox name;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialImageUploader imageUploader;
    @UiField
    MaterialTitle title;
    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialTextArea description;
    @UiField
    com.geocento.webapps.eobroker.common.client.widgets.material.MaterialRichEditor fullDescription;
    @UiField
    MaterialRow consortium;
    @UiField
    MaterialDatePicker date;
    @UiField
    CompanyTextBox customer;
    @UiField
    MaterialLink viewClient;

    public SuccessStoryViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        template.setPlace("stories");
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
    public HasClickHandlers getSubmit() {
        return submit;
    }

    @Override
    public String getImageUrl() {
        return imageUploader.getImageUrl();
    }

    @Override
    public void setIconUrl(String iconURL) {
        imageUploader.setImageUrl(iconURL);
    }

    @Override
    public HasText getDescription() {
        return description;
    }

    @Override
    public void setDate(Date date) {
        this.date.setDate(date);
    }

    @Override
    public Date getDate() {
        return date.getDate();
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
    public List<CompanyRoleDTO> getConsortium() {
        ArrayList<CompanyRoleDTO> companyRoles = new ArrayList<CompanyRoleDTO>();
        for(int index = 0; index < consortium.getWidgetCount(); index++) {
            Widget widget = consortium.getWidget(index);
            if(widget instanceof CompanyRoleWidget) {
                companyRoles.add(((CompanyRoleWidget) widget).getCompanyRole());
            }
        }
        return companyRoles;
    }

    @Override
    public void setConsortium(List<CompanyRoleDTO> companyRoleDTOs) {
        consortium.clear();
        if(companyRoleDTOs == null || companyRoleDTOs.size() == 0) {
            consortium.add(new MaterialLabel("No company added yet, use the add company button to add a company"));
        } else {
            for(CompanyRoleDTO companyRoleDTO : companyRoleDTOs) {
                addCompanyRole(companyRoleDTO);
            }
        }
    }

    private void addCompanyRole(CompanyRoleDTO companyRoleDTO) {
        // make sure we remove the text before
        if(getConsortium().size() == 0) {
            consortium.clear();
        }
        final CompanyRoleWidget companyRoleWidget = new CompanyRoleWidget(companyRoleDTO);
        companyRoleWidget.getRemove().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WidgetUtil.removeWidgets(consortium, new WidgetUtil.CheckValue() {
                    @Override
                    public boolean isValue(Widget widget) {
                        return widget instanceof CompanyRoleWidget && ((CompanyRoleWidget) widget) == companyRoleWidget;
                    }
                });
            }
        });
        consortium.add(companyRoleWidget);
    }

    @Override
    public HasClickHandlers getViewClient() {
        return viewClient;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

}