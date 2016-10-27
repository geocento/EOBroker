package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveDataset;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveDatasetHandler;
import com.geocento.webapps.eobroker.supplier.client.places.DatasetProviderPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.shared.dtos.DatasetProviderDTO;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.views.DatasetProviderView;
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
public class DatasetProviderActivity extends TemplateActivity implements DatasetProviderView.Presenter {

    private DatasetProviderView companyView;

    private DatasetProviderDTO datasetProviderDTO;

    public DatasetProviderActivity(DatasetProviderPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        companyView = clientFactory.getDatasetProviderView();
        companyView.setPresenter(this);
        panel.setWidget(companyView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        Long datasetId = null;
        if(tokens.containsKey(DatasetProviderPlace.TOKENS.id.toString())) {
            datasetId = Long.parseLong(tokens.get(DatasetProviderPlace.TOKENS.id.toString()));
        }
        if(datasetId != null) {
            // load all companys
            try {
                REST.withCallback(new MethodCallback<DatasetProviderDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, DatasetProviderDTO response) {
                        setDatasetProvider(response);
                    }
                }).call(ServicesUtil.assetsService).getDatasetProvider(datasetId);
            } catch (RequestException e) {
            }
        } else {
            DatasetProviderDTO company = new DatasetProviderDTO();
            setDatasetProvider(company);
        }
    }

    private void setDatasetProvider(DatasetProviderDTO datasetProviderDTO) {
        this.datasetProviderDTO = datasetProviderDTO;
        companyView.setTitleLine(datasetProviderDTO.getId() == null ? "Create dataset" : "Edit dataset");
        companyView.getName().setText(datasetProviderDTO.getName());
        companyView.setIconUrl(datasetProviderDTO.getIconURL());
        companyView.getUri().setText(datasetProviderDTO.getUri());
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(companyView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                datasetProviderDTO.setName(companyView.getName().getText());
                datasetProviderDTO.setIconURL(companyView.getIconUrl());
                datasetProviderDTO.setUri(companyView.getUri().getText());
                companyView.setLoading("Saving dataset...");
                try {
                    REST.withCallback(new MethodCallback<Long>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            companyView.setLoadingError("Error saving company");
                        }

                        @Override
                        public void onSuccess(Method method, Long companyId) {
                            companyView.hideLoading("DatasetProvider saved");
                            datasetProviderDTO.setId(companyId);
                        }
                    }).call(ServicesUtil.assetsService).saveDatasetProvider(datasetProviderDTO);
                } catch (RequestException e) {
                }
            }
        }));
    }

}
