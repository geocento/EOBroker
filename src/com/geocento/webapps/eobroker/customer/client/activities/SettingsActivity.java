package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.SettingsPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.SettingsView;
import com.geocento.webapps.eobroker.customer.shared.SettingsDTO;
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

    private SettingsDTO settingsDTO;

    public SettingsActivity(SettingsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        settingsView = clientFactory.getSettingsView();
        settingsView.setPresenter(this);
        setTemplateView(settingsView.asWidget());
        selectMenu("settings");
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(settingsView.getSaveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                settingsDTO.setUserIconUrl(settingsView.getIconUrl());
                settingsDTO.setFullName(settingsView.getFullName().getText());
                settingsDTO.setEmail(settingsView.getEmail().getText());
                displayFullLoading("Saving settings...");
                try {
                    REST.withCallback(new MethodCallback<Void>() {

                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideFullLoading();
                            displayError("Problem saving settings");
                        }

                        @Override
                        public void onSuccess(Method method, Void result) {
                            hideFullLoading();
                            displaySuccess("Settings saved");
                        }

                    }).call(ServicesUtil.assetsService).saveUserSettings(settingsDTO);
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    private void handleHistory() {

        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        try {
            REST.withCallback(new MethodCallback<SettingsDTO>() {

                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideFullLoading();
                    Window.alert("Problem loading settings");
                }

                @Override
                public void onSuccess(Method method, SettingsDTO settingsDTO) {
                    hideFullLoading();
                    SettingsActivity.this.settingsDTO = settingsDTO;
                    settingsView.getFullName().setText(settingsDTO.getFullName());
                    settingsView.setIconUrl(settingsDTO.getUserIconUrl());
                    settingsView.getEmail().setText(settingsDTO.getEmail());
                    settingsView.setCompany(settingsDTO.getCompanyDTO());
                }

            }).call(ServicesUtil.assetsService).getUserSettings();
        } catch (RequestException e) {
            e.printStackTrace();
        }

    }

}
