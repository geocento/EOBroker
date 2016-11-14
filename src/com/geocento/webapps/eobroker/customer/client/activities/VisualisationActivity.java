package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.VisualisationPlace;
import com.geocento.webapps.eobroker.customer.client.views.VisualisationView;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class VisualisationActivity extends TemplateActivity implements VisualisationView.Presenter {

    private VisualisationView visualisationView;

    public VisualisationActivity(VisualisationPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        visualisationView = clientFactory.getVisualisationView();
        visualisationView.setPresenter(this);
        panel.setWidget(visualisationView.asWidget());
        setTemplateView(visualisationView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        visualisationView.setMapLoadedHandler(new Callback<Void, Exception>() {
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

        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        if(tokens.containsKey(VisualisationPlace.TOKENS.uri.toString())) {
            String uri = URL.decodeQueryString(tokens.get(VisualisationPlace.TOKENS.uri.toString()));
            viewResources(uri);
        }
    }

    private void viewResources(String uri) {
        // fow now we only deal with WMS
        String[] uriTokens = uri.split("::");
        switch (uriTokens[0]) {
            case "wms":
                // TODO - load WMS description
                String wmsUrl = uriTokens[1];
                String layerName = uriTokens[2];
                visualisationView.addWMSLayer(wmsUrl, layerName);
                break;
        }
    }

    @Override
    protected void bind() {
        super.bind();
    }

}
