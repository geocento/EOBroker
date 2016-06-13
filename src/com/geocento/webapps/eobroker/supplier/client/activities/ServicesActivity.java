package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.ServicesPlace;
import com.geocento.webapps.eobroker.supplier.client.views.ServicesView;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class ServicesActivity extends AbstractApplicationActivity implements ServicesView.Presenter {

    private ServicesView servicesView;

    public ServicesActivity(ServicesPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        servicesView = clientFactory.getServicesView();
        servicesView.setPresenter(this);
        panel.setWidget(servicesView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
    }

    @Override
    protected void bind() {
    }

}
