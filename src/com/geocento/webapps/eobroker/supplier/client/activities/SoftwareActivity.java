package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.SoftwarePlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.SoftwareView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SoftwareDTO;
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
public class SoftwareActivity extends TemplateActivity implements SoftwareView.Presenter {

    private SoftwareView softwareView;

    private SoftwareDTO softwareDTO;

    public SoftwareActivity(SoftwarePlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        softwareView = clientFactory.getSoftwareView();
        softwareView.setPresenter(this);
        setTemplateView(softwareView.getTemplateView());
        panel.setWidget(softwareView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        Long softwareId = null;
        if(tokens.containsKey(SoftwarePlace.TOKENS.id.toString())) {
            softwareId = Long.parseLong(tokens.get(SoftwarePlace.TOKENS.id.toString()));
        }
        if(softwareId != null) {
            try {
                REST.withCallback(new MethodCallback<SoftwareDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, SoftwareDTO response) {
                        setSoftware(response);
                    }
                }).call(ServicesUtil.assetsService).getSoftware(softwareId);
            } catch (RequestException e) {
            }
        } else {
            SoftwareDTO softwareDTO = new SoftwareDTO();
            setSoftware(softwareDTO);
        }
    }

    private void setSoftware(SoftwareDTO softwareDTO) {
        this.softwareDTO = softwareDTO;
        softwareView.setTitleLine(softwareDTO.getId() == null ? "Create software" : "Edit software");
        softwareView.getName().setText(softwareDTO.getName());
        softwareView.setIconUrl(softwareDTO.getImageUrl());
        softwareView.getDescription().setText(softwareDTO.getDescription());
        softwareView.setFullDescription(softwareDTO.getFullDescription());
        softwareView.setSelectedProducts(softwareDTO.getProducts());
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(softwareView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                softwareDTO.setName(softwareView.getName().getText());
                softwareDTO.setImageUrl(softwareView.getImageUrl());
                softwareDTO.setDescription(softwareView.getDescription().getText());
                softwareDTO.setFullDescription(softwareView.getFullDescription());
                softwareDTO.setProducts(softwareView.getSelectedProducts());
                displayLoading("Saving software...");
                try {
                    REST.withCallback(new MethodCallback<Long>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError("Error saving software");
                        }

                        @Override
                        public void onSuccess(Method method, Long companyId) {
                            hideLoading();
                            displaySuccess("Software saved");
                            softwareDTO.setId(companyId);
                        }
                    }).call(ServicesUtil.assetsService).saveSoftware(softwareDTO);
                } catch (RequestException e) {
                }
            }
        }));
    }

}