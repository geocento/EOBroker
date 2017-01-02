package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.views.TemplateView;
import com.geocento.webapps.eobroker.admin.client.widgets.CodeEditor;
import com.geocento.webapps.eobroker.admin.client.widgets.FormEditor;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.shared.entities.Sector;
import com.geocento.webapps.eobroker.common.shared.entities.Thematic;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.richeditor.MaterialRichEditor;
import gwt.material.design.client.ui.*;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class NewsItemViewImpl extends Composite implements NewsItemView {

    interface NewsItemViewUiBinder extends UiBinder<Widget, NewsItemViewImpl> {
    }

    private static NewsItemViewUiBinder ourUiBinder = GWT.create(NewsItemViewUiBinder.class);

    public static interface Style extends CssResource {
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialTitle pageTitle;
    @UiField
    MaterialTextBox title;
    @UiField
    MaterialTextArea shortDescription;
    @UiField
    MaterialImageUploader imageUploader;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialTextBox websiteUrl;

    private Presenter presenter;

    public NewsItemViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        title.setText("List of saved news items");
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
    public HasText getNewsItemTitle() {
        return title;
    }

    @Override
    public HasText getDescription() {
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
    public HasText getWebsiteUrl() {
        return websiteUrl;
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
    public void setPageTitle(String title) {
        pageTitle.setTitle(title);
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

}