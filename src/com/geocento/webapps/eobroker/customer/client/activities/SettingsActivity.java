package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.SettingsPlace;
import com.geocento.webapps.eobroker.customer.client.views.SettingsView;
import com.geocento.webapps.eobroker.customer.shared.SettingsDTO;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

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

    private void handleHistory() {

        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

    }

}
