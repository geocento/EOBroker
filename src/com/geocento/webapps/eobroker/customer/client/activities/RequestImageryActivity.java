package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.RequestImageryPlace;
import com.geocento.webapps.eobroker.customer.client.views.RequestImageryView;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Created by thomas on 09/05/2016.
 */
public class RequestImageryActivity extends TemplateActivity implements RequestImageryView.Presenter {

    private RequestImageryView requestImageryView;

    public RequestImageryActivity(RequestImageryPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        requestImageryView = clientFactory.getRequestImageryView();
        requestImageryView.setPresenter(this);
        panel.setWidget(requestImageryView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        requestImageryView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Error " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                handleHistory();
            }
        });
    }

    private void handleHistory() {
        requestImageryView.displayAoI(Customer.currentAoI);
    }

    @Override
    protected void bind() {
        super.bind();

   }

    @Override
    public void aoiChanged(AoI aoi) {

    }
}
