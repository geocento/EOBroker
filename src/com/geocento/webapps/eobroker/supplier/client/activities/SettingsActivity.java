package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.SettingsPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.DashboardView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierSettingsDTO;
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
public class SettingsActivity extends TemplateActivity implements DashboardView.Presenter {

    private DashboardView dashboardView;

    public SettingsActivity(SettingsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        dashboardView = clientFactory.getDashboardView();
        dashboardView.setPresenter(this);
        setTemplateView(dashboardView.getTemplateView());
        panel.setWidget(dashboardView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        try {
            displayFullLoading("Loading settings...");
            REST.withCallback(new MethodCallback<SupplierSettingsDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideFullLoading();
                    Window.alert("Error loading settings...");
                }

                @Override
                public void onSuccess(Method method, SupplierSettingsDTO response) {
                    hideFullLoading();
                    dashboardView.displaySettings(response);
                }

            }).call(ServicesUtil.assetsService).getSettings();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(dashboardView.getSaveSettings().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                SupplierSettingsDTO supplierSettings;
                try {
                    supplierSettings = dashboardView.getSettings();
                } catch (Exception e) {
                    displayError("Error with settings, " + e.getMessage());
                    return;
                }
                displayLoading("Saving settings");
                try {
                    REST.withCallback(new MethodCallback<Void>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideFullLoading();
                            displayError("Error saving settings...");
                        }

                        @Override
                        public void onSuccess(Method method, Void response) {
                            hideFullLoading();
                            displaySuccess("Settings saved");
                        }

                    }).call(ServicesUtil.assetsService).saveSettings(supplierSettings);
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));

    }

}
