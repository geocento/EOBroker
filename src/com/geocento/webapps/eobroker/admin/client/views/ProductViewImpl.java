package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.widgets.CodeEditor;
import com.geocento.webapps.eobroker.admin.client.widgets.FormEditor;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.shared.entities.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.Sector;
import com.geocento.webapps.eobroker.common.shared.entities.Thematic;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.richeditor.MaterialRichEditor;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialListBox;
import gwt.material.design.client.ui.MaterialTextBox;
import gwt.material.design.client.ui.MaterialTitle;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductViewImpl extends Composite implements ProductView {

    interface ProductViewUiBinder extends UiBinder<Widget, ProductViewImpl> {
    }

    private static ProductViewUiBinder ourUiBinder = GWT.create(ProductViewUiBinder.class);

    public static interface Style extends CssResource {

        String navOpened();
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
    MaterialRichEditor fullDescription;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialImageUploader imageUploader;
    @UiField
    MaterialListBox thematic;
    @UiField
    MaterialListBox sector;
    @UiField
    FormEditor formFields;
    @UiField
    CodeEditor recommendationRule;
    @UiField
    FormEditor apiFormFields;

    private Presenter presenter;

    public ProductViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        for(Sector sector : Sector.values()) {
            this.sector.addItem(sector.toString(), sector.name());
        }

        for(Thematic thematic : Thematic.values()) {
            this.thematic.addItem(thematic.toString(), thematic.name());
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
        return Sector.valueOf(sector.getSelectedValue());
    }

    @Override
    public void setSector(Sector sector) {
        this.sector.setSelectedValue(sector.name());
    }

    @Override
    public Thematic getThematic() {
        return Thematic.valueOf(thematic.getSelectedValue());
    }

    @Override
    public void setThematic(Thematic thematic) {
        this.thematic.setSelectedValue(thematic.name());
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
        this.recommendationRule.startEditing(recommendationRule);
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

}