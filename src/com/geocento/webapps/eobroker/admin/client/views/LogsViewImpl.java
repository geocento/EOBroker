package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialTextArea;
import gwt.material.design.client.ui.MaterialTextBox;

/**
 * Created by thomas on 09/05/2016.
 */
public class LogsViewImpl extends Composite implements LogsView {

    interface TemplateUiBinder extends UiBinder<Widget, LogsViewImpl> {
    }

    private static TemplateUiBinder ourUiBinder = GWT.create(TemplateUiBinder.class);

    public static interface Style extends CssResource {

        String navOpened();
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    HTMLPanel logsPanel;
    @UiField
    MaterialButton reload;

    private Presenter presenter;

    public LogsViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setLogs(String response) {
        logsPanel.clear();
        MaterialTextArea materialTextArea = new MaterialTextArea();
        materialTextArea.setText(response);
        logsPanel.add(materialTextArea);
    }

    @Override
    public HasClickHandlers getReload() {
        return reload;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}