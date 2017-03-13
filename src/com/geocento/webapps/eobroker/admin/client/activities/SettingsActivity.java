package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.SettingsPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.SettingsView;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.ApplicationSettings;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class SettingsActivity extends TemplateActivity implements SettingsView.Presenter {

    private SettingsView settingsView;

    private ApplicationSettings settings;

    public SettingsActivity(SettingsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        settingsView = clientFactory.getSettingsView();
        settingsView.setPresenter(this);
        panel.setWidget(settingsView.asWidget());
        setTemplateView(settingsView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        try {
            displayLoading("Loading settings");
            REST.withCallback(new MethodCallback<ApplicationSettings>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideLoading();
                    displayError(exception.getMessage());
                }

                @Override
                public void onSuccess(Method method, ApplicationSettings response) {
                    hideLoading();
                    setSettings(response);
                }
            }).call(ServicesUtil.assetsService).getSettings();
        } catch (RequestException e) {
        }
    }

    private void setSettings(ApplicationSettings settings) {
        settingsView.setSettings(settings);
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(settingsView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                displayLoading("Saving settings...");
                try {
                    REST.withCallback(new MethodCallback<Void>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            displayError("Error saving settings");
                        }

                        @Override
                        public void onSuccess(Method method, Void result) {
                            hideLoading();
                            displaySuccess("Settings updated");
                        }
                    }).call(ServicesUtil.assetsService).saveSettings(settingsView.getSettings());
                } catch (RequestException e) {
                } catch (Exception e) {
                    displayError(e.getMessage());
                }
            }
        }));
    }

}
