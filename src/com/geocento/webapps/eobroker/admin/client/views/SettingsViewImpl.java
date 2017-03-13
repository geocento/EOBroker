package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.common.client.widgets.forms.ElementEditor;
import com.geocento.webapps.eobroker.common.client.widgets.forms.FormHelper;
import com.geocento.webapps.eobroker.common.shared.entities.ApplicationSettings;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialLabel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class SettingsViewImpl extends Composite implements SettingsView {

    interface TemplateUiBinder extends UiBinder<Widget, SettingsViewImpl> {
    }

    private static TemplateUiBinder ourUiBinder = GWT.create(TemplateUiBinder.class);

    public static interface Style extends CssResource {

        String navOpened();

        String editor();

        String title();
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    HTMLPanel settingsPanel;
    @UiField
    MaterialButton submit;

    private ApplicationSettings settings;

    private Presenter presenter;

    public SettingsViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSettings(ApplicationSettings settings) {
        this.settings = settings;
        settingsPanel.clear();
        addTitle("Generic settings");
        addTextEditor(settings.getApplicationName(), "Application Name", "The name for this application", 5, 100);
        addTextEditor(settings.getAbout(), "About", "Add a link to the about broker URL", 5, 1000);
        addTitle("Email settings");
        addTextEditor(settings.getEmailServer(), "Server", "The email server", 5, 100);
        addTextEditor(settings.getEmailFrom(), "From", "The from email address for the emails", 5, 100);
        addTextEditor(settings.getEmailAccount(), "Account", "The account for the email", 5, 100);
        addTextEditor(settings.getEmailPassword(), "Password", "The password for the email account", 5, 100);
        addIntegerEditor(settings.getEmailPort(), "Port", "The port for the email server");
        addBooleanEditor(settings.isSmtps(), "SMTPS", "If the email server uses SMPTS");
        addBooleanEditor(settings.isEnableTLS(), "Enable TLS", "If the email server uses TLS");
    }

    @Override
    public ApplicationSettings getSettings() throws Exception {
        // update settings
        List<FormElementValue> values = getFormElementValues();
        int index = 0;
        // generic settings
        settings.setApplicationName(String.valueOf(values.get(index++).getValue()));
        settings.setAbout(String.valueOf(values.get(index++).getValue()));
        // email settings
        settings.setEmailServer(String.valueOf(values.get(index++).getValue()));
        settings.setEmailFrom(String.valueOf(values.get(index++).getValue()));
        settings.setEmailAccount(String.valueOf(values.get(index++).getValue()));
        settings.setEmailPassword(String.valueOf(values.get(index++).getValue()));
        settings.setEmailPort(Integer.valueOf(values.get(index++).getValue()));
        settings.setSmtps(Boolean.valueOf(values.get(index++).getValue()));
        settings.setEnableTLS(Boolean.valueOf(values.get(index++).getValue()));
        return settings;
    }

    private void addTitle(String title) {
        MaterialLabel materialLabel = new MaterialLabel(title);
        materialLabel.addStyleName(style.title());
        settingsPanel.add(materialLabel);
    }

    private void addTextEditor(String value, String name, String description, int min, int max) {
        TextFormElement textFormElement = new TextFormElement();
        textFormElement.setName(name);
        textFormElement.setDescription(description);
        textFormElement.setMin(min);
        textFormElement.setMax(max);
        ElementEditor editor = createEditor(textFormElement);
        // TODO - check this is OK
        if(value != null) {
            editor.setFormElementValue(value.toString());
        }
    }

    private void addIntegerEditor(Integer value, String name, String description) {
        IntegerFormElement integerFormElement = new IntegerFormElement();
        integerFormElement.setName(name);
        integerFormElement.setDescription(description);
        ElementEditor editor = createEditor(integerFormElement);
        // TODO - check this is OK
        if(value != null) {
            editor.setFormElementValue(value.toString());
        }
    }

    private void addBooleanEditor(Boolean value, String name, String description) {
        BooleanFormElement booleanFormElement = new BooleanFormElement();
        booleanFormElement.setName(name);
        booleanFormElement.setDescription(description);
        ElementEditor editor = createEditor(booleanFormElement);
        // TODO - check this is OK
        if(value != null) {
            editor.setFormElementValue(value.toString());
        }
    }

    private ElementEditor createEditor(FormElement formElement) {
        ElementEditor editor = FormHelper.createEditor(formElement);
        editor.addStyleName(style.editor());
        settingsPanel.add(editor);
        return editor;
    }

    public List<FormElementValue> getFormElementValues() throws Exception {
        List<FormElementValue> formElementValues = new ArrayList<FormElementValue>();
        for(int index = 0; index < settingsPanel.getWidgetCount(); index++) {
            Widget widget = settingsPanel.getWidget(index);
            if(widget instanceof ElementEditor) {
                formElementValues.add(((ElementEditor) widget).getFormElementValue());
            }
        }
        return formElementValues;
    }

    @Override
    public HasClickHandlers getSubmit() {
        return submit;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}