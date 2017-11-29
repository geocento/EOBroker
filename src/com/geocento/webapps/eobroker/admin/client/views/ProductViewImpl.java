package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.widgets.CodeEditor;
import com.geocento.webapps.eobroker.admin.client.widgets.FormEditor;
import com.geocento.webapps.eobroker.admin.client.widgets.GeoinformationWidget;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.client.widgets.material.*;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.MaterialListValueBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductViewImpl extends Composite implements ProductView {

    interface ProductViewUiBinder extends UiBinder<Widget, ProductViewImpl> {
    }

    private static ProductViewUiBinder ourUiBinder = GWT.create(ProductViewUiBinder.class);

    public static interface Style extends CssResource {
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialTitle title;
    @UiField
    MaterialTextBox name;
    @UiField
    MaterialTextBox shortDescription;
    @UiField
    com.geocento.webapps.eobroker.common.client.widgets.material.MaterialRichEditor fullDescription;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialImageUploader imageUploader;
    @UiField
    MaterialListValueBox<Thematic> thematic;
    @UiField
    MaterialListValueBox<Sector> sector;
    @UiField
    FormEditor formFields;
    @UiField
    CodeEditor recommendationRule;
    @UiField
    FormEditor apiFormFields;
    @UiField
    MaterialButton addGeoinformation;
    @UiField
    MaterialRow geoinformation;
    @UiField
    MaterialLabel geoinformationMessage;
    @UiField
    MaterialLabel performanceMessage;
    @UiField
    MaterialRow performances;
    @UiField
    MaterialButton addPerformance;
    @UiField
    MaterialListValueBox<ProductCategory> categories;

    private Presenter presenter;

    public ProductViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        for(Sector sector : Sector.values()) {
            this.sector.addItem(sector, sector.getName());
        }

        for(Thematic thematic : Thematic.values()) {
            this.thematic.addItem(thematic, thematic.getName());
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
    public HasText getName() {
        return name;
    }

    @Override
    public HasText getShortDescription() {
        return shortDescription;
    }

    @Override
    public String getImageUrl() {
        return imageUploader.getImageUrl();
    }

    @Override
    public void setImageUrl(String imageUrl) {
        imageUploader.setImageUrl(imageUrl);
    }

    @Override
    public String getDescription() {
        return fullDescription.getHTML();
    }

    @Override
    public void setDescription(String description) {
        fullDescription.setHTML(description);
    }

    @Override
    public Sector getSector() {
        return sector.getSelectedValue();
    }

    @Override
    public void setSector(Sector sector) {
        if(sector == null) {
            this.sector.setValue(Sector.all);
        } else {
            this.sector.setValue(sector);
        }
    }

    @Override
    public Thematic getThematic() {
        return thematic.getSelectedValue();
    }

    @Override
    public void setThematic(Thematic thematic) {
        if(thematic == null) {
            this.thematic.setValue(Thematic.all);
        } else {
            this.thematic.setValue(thematic);
        }
    }

    @Override
    public List<FormElement> getFormFields() {
        return formFields.getElements();
    }

    @Override
    public void setFormFields(List<FormElement> fields) {
        formFields.setElements(fields);
    }

    @Override
    public HasClickHandlers getSubmit() {
        return submit;
    }

    @Override
    public void setLoading(String message) {
        template.setLoading(message);
    }

    @Override
    public void setLoadingError(String message) {
        template.setLoadingError(message);
    }

    @Override
    public void hideLoading(String message) {
        template.hideLoading(message);
    }

    @Override
    public void setRecommendationRule(String recommendationRule) {
        this.recommendationRule.startEditing(recommendationRule == null ? "" : recommendationRule);
    }

    @Override
    public void setAPIFields(List<FormElement> apiFields) {
        apiFormFields.setElements(apiFields);
    }

    @Override
    public List<FormElement> getAPIFields() {
        return apiFormFields.getElements();
    }

    @Override
    public String getRecommendationRule() {
        return recommendationRule.getCode();
    }

    @Override
    public void setGeoinformation(List<FeatureDescription> geoinformation) {
        this.geoinformation.clear();
        if(geoinformation != null) {
            for(FeatureDescription featureDescription : geoinformation) {
                addGeoinformation(featureDescription);
            }
        }
        updateGeoinformationMessage();
    }

    private void addGeoinformation(FeatureDescription featureDescription) {
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        this.geoinformation.add(materialColumn);
        GeoinformationWidget geoinformationWidget = new GeoinformationWidget(featureDescription);
        materialColumn.add(geoinformationWidget);
        geoinformationWidget.getRemove().addClickHandler(event -> {
            if (Window.confirm("Are you sure you want to remove this geoinformation?")) {
                this.geoinformation.remove(materialColumn);
                updateGeoinformationMessage();
            }
        });
    }

    private void updateGeoinformationMessage() {
        List<FeatureDescription> geoinformations = getGeoinformation();
        geoinformationMessage.setText(geoinformations.size() == 0 ? "No geoinformation provided, add new geoinformation using the button below" :
                geoinformations.size() + " geoinformation defined, add more using the add button below");
    }

    @Override
    public List<FeatureDescription> getGeoinformation() {
        List<FeatureDescription> featureDescriptions = new ArrayList<FeatureDescription>();
        for(int index = 0; index < geoinformation.getWidgetCount(); index++) {
            featureDescriptions.add(((GeoinformationWidget) ((MaterialColumn) geoinformation.getWidget(index)).getWidget(0)).getFeatureDescription());
        }
        return featureDescriptions;
    }

    @UiHandler("addGeoinformation")
    void addGeoInformation(ClickEvent clickEvent) {
        addGeoinformation(new FeatureDescription());
    }

    @Override
    public void setPerformances(List<PerformanceDescription> performanceDescriptions) {
        this.performances.clear();
        if(performanceDescriptions != null) {
            for(PerformanceDescription performanceDescription : performanceDescriptions) {
                addPerformance(performanceDescription);
            }
        }
        updatePerformanceMessage();
    }

    private void addPerformance(PerformanceDescription performanceDescription) {
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        this.performances.add(materialColumn);
        PerformanceWidget performanceWidget = new PerformanceWidget(performanceDescription);
        materialColumn.add(performanceWidget);
        performanceWidget.getRemove().addClickHandler(event -> {
            if (Window.confirm("Are you sure you want to remove this performance?")) {
                this.performances.remove(materialColumn);
                updatePerformanceMessage();
            }
        });
    }

    private void updatePerformanceMessage() {
        List<PerformanceDescription> performanceDescriptions = getPerformances();
        performanceMessage.setText(performanceDescriptions.size() == 0 ? "No performances provided, add new performance criteria using the button below" :
                performanceDescriptions.size() + " performances defined, add more using the add button below");
    }

    @Override
    public List<PerformanceDescription> getPerformances() {
        List<PerformanceDescription> performanceDescriptions = new ArrayList<PerformanceDescription>();
        for(int index = 0; index < performances.getWidgetCount(); index++) {
            performanceDescriptions.add(((PerformanceWidget) ((MaterialColumn) performances.getWidget(index)).getWidget(0)).getPerformanceDescription());
        }
        return performanceDescriptions;
    }

    @UiHandler("addPerformance")
    void addPerformance(ClickEvent clickEvent) {
        addPerformance(new PerformanceDescription());
    }

    @Override
    public void setAvailableCategories(List<ProductCategory> availableCategories) {
        this.categories.clear();
        for(ProductCategory productCategory : availableCategories) {
            categories.addItem(productCategory, productCategory.getName());
        }
    }

    @Override
    public void setCategories(List<ProductCategory> categories) {
        List<Long> ids = ListUtil.mutate(categories, productCategory -> productCategory.getId());
        for(int index = 0; index < this.categories.getItemCount(); index++) {
            this.categories.setItemSelected(index, categories != null && ids.contains(this.categories.getValue(index).getId()));
        }
    }

    @Override
    public List<ProductCategory> getCategories() {
        List<ProductCategory> selectedCategories = new ArrayList<ProductCategory>();
        for(int index = 0; index < this.categories.getItemCount(); index++) {
            if(this.categories.isItemSelected(index)) {
                selectedCategories.add(this.categories.getValue(index));
            }
        }
        return selectedCategories;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

}