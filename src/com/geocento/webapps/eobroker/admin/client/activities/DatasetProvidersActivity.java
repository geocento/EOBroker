package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.CompanyPlace;
import com.geocento.webapps.eobroker.admin.client.places.DatasetProvidersPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.DatasetProvidersView;
import com.geocento.webapps.eobroker.admin.shared.dtos.DatasetProviderDTO;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
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
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class DatasetProvidersActivity extends TemplateActivity implements DatasetProvidersView.Presenter {

    private DatasetProvidersView datasetprovidersView;

    public DatasetProvidersActivity(DatasetProvidersPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        datasetprovidersView = clientFactory.getDatasetProvidersView();
        datasetprovidersView.setPresenter(this);
        panel.setWidget(datasetprovidersView.asWidget());
        setTemplateView(datasetprovidersView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        // load all products
        try {
            REST.withCallback(new MethodCallback<List<DatasetProviderDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<DatasetProviderDTO> response) {
                    datasetprovidersView.setDatasets(response);
                }
            }).call(ServicesUtil.assetsService).listDatasets();
        } catch (RequestException e) {
        }
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(datasetprovidersView.getCreateNewButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new CompanyPlace());
            }
        }));
    }

}
