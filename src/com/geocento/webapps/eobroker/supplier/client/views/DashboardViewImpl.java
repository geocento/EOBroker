package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.forms.FormCreator;
import com.geocento.webapps.eobroker.common.shared.entities.NOTIFICATION_DELAY;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.widgets.*;
import com.geocento.webapps.eobroker.supplier.shared.dtos.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class DashboardViewImpl extends Composite implements DashboardView {

    private Presenter presenter;

    interface DashboardViewUiBinder extends UiBinder<Widget, DashboardViewImpl> {
    }

    private static DashboardViewUiBinder ourUiBinder = GWT.create(DashboardViewUiBinder.class);

    @UiField
    MaterialRow services;
    @UiField
    MaterialButton editCompany;
    @UiField
    MaterialButton addService;
    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialButton addDataset;
    @UiField
    MaterialRow datasets;
    @UiField
    MaterialButton addProductDataset;
    @UiField
    MaterialRow productdatasets;
    @UiField
    MaterialTitle companyTitle;
    @UiField
    MaterialImage companyImage;
    @UiField
    MaterialRow softwares;
    @UiField
    MaterialButton addSoftware;
    @UiField
    MaterialRow projects;
    @UiField
    MaterialButton addProject;
    @UiField
    MaterialPanel companyPanel;
    @UiField
    MaterialPanel servicesPanel;
    @UiField
    MaterialPanel datasetsPanel;
    @UiField
    MaterialPanel softwarePanel;
    @UiField
    MaterialPanel projectsPanel;
    @UiField
    MaterialPanel datasetServicesPanel;
    @UiField
    MaterialPanel messagePanel;
    @UiField
    MaterialPanel settingsPanel;
    @UiField
    FormCreator settings;
    @UiField
    MaterialButton saveSettings;
    @UiField
    MaterialRow successStories;
    @UiField
    MaterialButton addSuccessStory;
    @UiField
    MaterialPanel testimonialsPanel;
    @UiField
    MaterialButton requestTestimonial;
    @UiField
    MaterialRow testimonials;
    @UiField
    MaterialPanel successStoriesPanel;

    public DashboardViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayServices(List<ProductServiceDTO> productServiceDTOs) {
        hideAll();
        template.setPlace("productservices");
        servicesPanel.setVisible(true);
        services.clear();
        if(productServiceDTOs == null || productServiceDTOs.size() == 0) {
            services.add(new MaterialLabel("No service, click on the button below to add a new service"));
            return;
        }
        for(ProductServiceDTO productServiceDTO : productServiceDTOs) {
            MaterialColumn materialColumn = new MaterialColumn(6, 4, 3);
            materialColumn.add(new ProductServiceWidget(productServiceDTO));
            services.add(materialColumn);
        }
    }

    @Override
    public void displayDatasets(List<DatasetProviderDTO> datasetProviderDTOs) {
        hideAll();
        template.setPlace(null);
        datasetsPanel.setVisible(true);
        datasets.clear();
        if(datasetProviderDTOs == null || datasetProviderDTOs.size() == 0) {
            datasets.add(new MaterialLabel("No datasets, click on the button below to add a new dataset"));
            return;
        }
        MaterialRow materialRow = new MaterialRow();
        datasets.add(materialRow);
        for(DatasetProviderDTO datasetProviderDTO : datasetProviderDTOs) {
            MaterialColumn materialColumn = new MaterialColumn(12, 4, 3);
            materialColumn.add(new DatasetProviderWidget(datasetProviderDTO));
            materialRow.add(materialColumn);
        }
    }

    @Override
    public void displayProductDatasets(List<ProductDatasetDTO> productDatasetDTOs) {
        hideAll();
        template.setPlace("productdatasets");
        datasetsPanel.setVisible(true);
        productdatasets.clear();
        if(productDatasetDTOs == null || productDatasetDTOs.size() == 0) {
            productdatasets.add(new MaterialLabel("No off the shelf data, click on the button below to add a new offer"));
            return;
        }
        MaterialRow materialRow = new MaterialRow();
        productdatasets.add(materialRow);
        for(ProductDatasetDTO productDatasetDTO : productDatasetDTOs) {
            MaterialColumn materialColumn = new MaterialColumn(12, 4, 3);
            materialColumn.add(new ProductDatasetWidget(productDatasetDTO));
            materialRow.add(materialColumn);
        }
    }

    private void hideAll() {
        messagePanel.setVisible(false);
        companyPanel.setVisible(false);
        servicesPanel.setVisible(false);
        datasetsPanel.setVisible(false);
        softwarePanel.setVisible(false);
        projectsPanel.setVisible(false);
        testimonialsPanel.setVisible(false);
        successStoriesPanel.setVisible(false);
        settingsPanel.setVisible(false);
        datasetServicesPanel.setVisible(false);
    }

    @Override
    public void displaySoftwares(List<SoftwareDTO> softwareDTOs) {
        hideAll();
        template.setPlace("software");
        softwarePanel.setVisible(true);
        softwares.clear();
        if(softwareDTOs == null || softwareDTOs.size() == 0) {
            softwares.add(new MaterialLabel("No software, click on the button below to add a new offer"));
            return;
        }
        MaterialRow materialRow = new MaterialRow();
        softwares.add(materialRow);
        for(SoftwareDTO softwareDTO : softwareDTOs) {
            MaterialColumn materialColumn = new MaterialColumn(12, 4, 3);
            materialColumn.add(new SoftwareWidget(softwareDTO));
            materialRow.add(materialColumn);
        }
    }

    @Override
    public void displayProjects(List<ProjectDTO> projectDTOs) {
        hideAll();
        template.setPlace("project");
        projectsPanel.setVisible(true);
        projects.clear();
        if(projectDTOs == null || projectDTOs.size() == 0) {
            projects.add(new MaterialLabel("No project, click on the button below to add a new offer"));
            return;
        }
        MaterialRow materialRow = new MaterialRow();
        projects.add(materialRow);
        for(ProjectDTO projectDTO : projectDTOs) {
            MaterialColumn materialColumn = new MaterialColumn(12, 4, 3);
            materialColumn.add(new ProjectWidget(projectDTO));
            materialRow.add(materialColumn);
        }
    }

    @Override
    public void displayMessage() {
        hideAll();
        template.setPlace(null);
        messagePanel.setVisible(true);
        messagePanel.clear();
        messagePanel.add(new MaterialTitle("Welcome to the supplier management interface", "Please use the navigation menus on the side to edit your company assets"));
    }

    @Override
    public HasClickHandlers getSaveSettings() {
        return saveSettings;
    }

    @Override
    public SupplierSettingsDTO getSettings() throws Exception {
        SupplierSettingsDTO supplierSettings = new SupplierSettingsDTO();
        List<FormElementValue> formElementValues = settings.getFormElementValues();
        int index = 0;
        supplierSettings.setNotificationDelayMessages(NOTIFICATION_DELAY.valueOf(formElementValues.get(index++).getValue()));
        supplierSettings.setNotificationDelayRequests(NOTIFICATION_DELAY.valueOf(formElementValues.get(index++).getValue()));
        return supplierSettings;
    }

    @Override
    public void displaySettings(SupplierSettingsDTO supplierSettings) {
        hideAll();
        template.setPlace("settings");
        settingsPanel.setVisible(true);
        settings.clear();
        settings.addChoiceEnumEditor(NOTIFICATION_DELAY.values(), supplierSettings.getNotificationDelayMessages(), "Notifications on messages", "Set the frequency at which notifications on messages will be sent by email");
        settings.addChoiceEnumEditor(NOTIFICATION_DELAY.values(), supplierSettings.getNotificationDelayRequests(), "Notifications on requests", "Set the frequency at which notifications on requests will be sent by email");
    }

    @Override
    public void displaySuccessStories(List<SuccessStoryDTO> successStoryDTOs) {
        hideAll();
        template.setPlace("stories");
        successStoriesPanel.setVisible(true);
        successStories.clear();
        if(successStoryDTOs == null || successStoryDTOs.size() == 0) {
            successStories.add(new MaterialLabel("No success story, click on the button to add a new success story"));
            return;
        }
        MaterialRow materialRow = new MaterialRow();
        successStories.add(materialRow);
        for(SuccessStoryDTO successStoryDTO : successStoryDTOs) {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
            materialColumn.add(new SuccessStoryWidget(successStoryDTO));
            materialRow.add(materialColumn);
        }
    }

    @Override
    public HasClickHandlers getAddSuccessStory() {
        return addSuccessStory;
    }

    @Override
    public void displayTestimonials(List<TestimonialDTO> testimonials) {
        hideAll();
        template.setPlace("testimonials");
        testimonialsPanel.setVisible(true);
        this.testimonials.clear();
        if(testimonials == null || testimonials.size() == 0) {
            this.testimonials.add(new MaterialLabel("No testimonials, request a testimonial using the request button"));
            return;
        }
        MaterialRow materialRow = new MaterialRow();
        this.testimonials.add(materialRow);
        for(TestimonialDTO testimonialDTO : testimonials) {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
            materialColumn.add(new TestimonialWidget(testimonialDTO));
            materialRow.add(materialColumn);
        }
    }

    @Override
    public HasClickHandlers getRequestTestimonial() {
        return requestTestimonial;
    }

    @Override
    public void setCompany(CompanyDTO companyDTO) {
        companyImage.setUrl(companyDTO.getIconURL());
        companyTitle.setTitle(companyDTO.getName());
        companyTitle.setDescription(companyDTO.getDescription());
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public HasClickHandlers getAddService() {
        return addService;
    }

    @Override
    public HasClickHandlers getAddProductDataset() {
        return addProductDataset;
    }

    @Override
    public HasClickHandlers getAddDataset() {
        return addDataset;
    }

    @Override
    public HasClickHandlers getAddSoftware() {
        return addSoftware;
    }

    @Override
    public HasClickHandlers getAddProject() {
        return addProject;
    }

    @Override
    public HasClickHandlers editCompany() {
        return editCompany;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

}