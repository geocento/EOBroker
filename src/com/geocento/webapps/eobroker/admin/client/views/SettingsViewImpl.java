package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.common.client.widgets.forms.FormCreator;
import com.geocento.webapps.eobroker.common.shared.entities.ApplicationSettings;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;

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
    MaterialButton submit;
    @UiField
    FormCreator formCreator;

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
        formCreator.clear();
        formCreator.addTitle("Generic settings");
        formCreator.addTextEditor(settings.getApplicationName(), "Application Name", "The name for this application", 5, 100);
        formCreator.addTextEditor(settings.getAbout(), "About", "Add a link to the about broker URL", 5, 1000);
        formCreator.addTextEditor(settings.getHelpLink(), "Help", "Link to the help page URL", 5, 1000);
        formCreator.addIntegerEditor(settings.getMaxSampleSizeMB(), "Max Sample size (MB)", "Max size in MB for the samples");
        formCreator.addTitle("Email settings");
        formCreator.addTextEditor(settings.getEmailServer(), "Server", "The email server", 5, 100);
        formCreator.addTextEditor(settings.getEmailFrom(), "From", "The from email address for the emails", 5, 100);
        formCreator.addTextEditor(settings.getEmailAccount(), "Account", "The account for the email", 5, 100);
        formCreator.addTextEditor(settings.getEmailPassword(), "Password", "The password for the email account", 5, 100);
        formCreator.addIntegerEditor(settings.getEmailPort(), "Port", "The port for the email server");
        formCreator.addBooleanEditor(settings.isSmtps(), "SMTPS", "If the email server uses SMPTS");
        formCreator.addBooleanEditor(settings.isEnableTLS(), "Enable TLS", "If the email server uses TLS");
        formCreator.addTitle("Geoserver parameters");
        formCreator.addTextEditor(settings.getGeoserverOWS(), "OWS URL", "The URL for the Geoserver OWS service", 5, 100);
        formCreator.addTextEditor(settings.getGeoserverRESTUri(), "REST URL", "The URL for the Geoserver REST API", 5, 100);
        formCreator.addTextEditor(settings.getGeoserverUser(), "User", "The user name for the Geoserver REST API", 5, 100);
        formCreator.addTextEditor(settings.getGeoserverPassword(), "Password", "The password for the Geoserver REST API", 5, 100);
        formCreator.addTitle("Internal parameters");
        formCreator.addTextEditor(settings.getDataDirectory(), "Data directory", "The path for the data directory on the local machine", 5, 100);
        formCreator.addTextEditor(settings.getServerType(), "Server type", "The server type, whether local, staging or production", 5, 100);
        formCreator.addTextEditor(settings.getWebsiteUrl(), "Website URL", "The website URL, used for links in emails", 5, 100);
        formCreator.addTextEditor(settings.getSupplierWebsiteUrl(), "Supplier Website URL", "The supplier website URL, used for links in emails", 5, 100);
        formCreator.addIntegerEditor(settings.getNotificationDelay(), "Notification delay on hours", "The notification delay for sending emails, expressed in hours");
    }

    @Override
    public ApplicationSettings getSettings() throws Exception {
        // update settings
        List<FormElementValue> values = formCreator.getFormElementValues();
        int index = 0;
        // generic settings
        settings.setApplicationName(String.valueOf(values.get(index++).getValue()));
        settings.setAbout(String.valueOf(values.get(index++).getValue()));
        settings.setHelpLink(String.valueOf(values.get(index++).getValue()));
        settings.setMaxSampleSizeMB(Integer.valueOf(values.get(index++).getValue()));
        // email settings
        settings.setEmailServer(String.valueOf(values.get(index++).getValue()));
        settings.setEmailFrom(String.valueOf(values.get(index++).getValue()));
        settings.setEmailAccount(String.valueOf(values.get(index++).getValue()));
        settings.setEmailPassword(String.valueOf(values.get(index++).getValue()));
        settings.setEmailPort(Integer.valueOf(values.get(index++).getValue()));
        settings.setSmtps(Boolean.valueOf(values.get(index++).getValue()));
        settings.setEnableTLS(Boolean.valueOf(values.get(index++).getValue()));
        // geoserver parameters
        settings.setGeoserverOWS(String.valueOf(values.get(index++).getValue()));
        settings.setGeoserverRESTUri(String.valueOf(values.get(index++).getValue()));
        settings.setGeoserverUser(String.valueOf(values.get(index++).getValue()));
        settings.setGeoserverPassword(String.valueOf(values.get(index++).getValue()));
        // internal parameters
        settings.setDataDirectory(String.valueOf(values.get(index++).getValue()));
        settings.setServerType(String.valueOf(values.get(index++).getValue()));
        settings.setWebsiteUrl(String.valueOf(values.get(index++).getValue()));
        settings.setSupplierWebsiteUrl(String.valueOf(values.get(index++).getValue()));
        settings.setNotificationDelay(Integer.valueOf(values.get(index++).getValue()));

        return settings;
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