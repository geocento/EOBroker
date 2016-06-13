package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.CompanyPlace;
import com.geocento.webapps.eobroker.supplier.client.views.CompanyView;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class CompanyActivity extends AbstractApplicationActivity implements CompanyView.Presenter {

    private CompanyView companyView;

    public CompanyActivity(CompanyPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        companyView = clientFactory.getCompanyView();
        companyView.setPresenter(this);
        panel.setWidget(companyView.asWidget());
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
