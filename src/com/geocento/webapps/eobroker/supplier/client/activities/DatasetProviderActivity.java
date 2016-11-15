package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.DatasetProviderPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.DatasetProviderView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.DatasetProviderDTO;
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

    private DatasetProviderView datasetProviderView;

    private DatasetProviderDTO datasetProviderDTO;

    public DatasetProviderActivity(DatasetProviderPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        datasetProviderView = clientFactory.getDatasetProviderView();
        datasetProviderView.setPresenter(this);
        setTemplateView(datasetProviderView.getTemplateView());
        panel.setWidget(datasetProviderView.asWidget());
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
        datasetProviderView.setTitleLine(datasetProviderDTO.getId() == null ? "Create dataset" : "Edit dataset");
        datasetProviderView.getName().setText(datasetProviderDTO.getName());
        datasetProviderView.setIconUrl(datasetProviderDTO.getIconURL());
        datasetProviderView.getUri().setText(datasetProviderDTO.getUri());
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(datasetProviderView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                datasetProviderDTO.setName(datasetProviderView.getName().getText());
                datasetProviderDTO.setIconURL(datasetProviderView.getIconUrl());
                datasetProviderDTO.setUri(datasetProviderView.getUri().getText());
                // do some checks
                try {
                    if (datasetProviderDTO.getName() == null || datasetProviderDTO.getName().length() < 3) {
                        throw new Exception("Please provide a valid name");
                    }
                    if (datasetProviderDTO.getIconURL() == null) {
                        throw new Exception("Please provide a picture");
                    }
                    if (datasetProviderDTO.getUri() == null || datasetProviderDTO.getUri().length() < 3) {
                        throw new Exception("Please provide a valid description");
                    }
                } catch (Exception e) {
                    // TODO - use the view instead
                    Window.alert(e.getMessage());
                    return;
                }
                displayLoading("Saving dataset...");
                try {
                    REST.withCallback(new MethodCallback<Long>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError("Error saving company");
                        }

                        @Override
                        public void onSuccess(Method method, Long companyId) {
                            hideLoading();
                            displaySuccess("Saved dataset provider");
                            datasetProviderDTO.setId(companyId);
                        }
                    }).call(ServicesUtil.assetsService).saveDatasetProvider(datasetProviderDTO);
                } catch (RequestException e) {
                }
            }
        }));
    }

}
