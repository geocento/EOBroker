package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.CompanyPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.CompanyView;
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
public class CompanyActivity extends TemplateActivity implements CompanyView.Presenter {

    private CompanyView companyView;

    private CompanyDTO companyDTO;

    public CompanyActivity(CompanyPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        companyView = clientFactory.getCompanyView();
        companyView.setPresenter(this);
        setTemplateView(companyView.getTemplateView());
        panel.setWidget(companyView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        companyView.setTitleLine("Edit your company details and settings");
        // no need for company id
        displayFullLoading("Loading company information...");
        try {
            REST.withCallback(new MethodCallback<CompanyDTO>() {

                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideFullLoading();
                    Window.alert("Problem loading company information");
                }

                @Override
                public void onSuccess(Method method, CompanyDTO companyDTO) {
                    hideFullLoading();
                    CompanyActivity.this.companyDTO = companyDTO;
                    companyView.getName().setText(companyDTO.getName());
                    companyView.getDescription().setText(companyDTO.getDescription());
                    companyView.getEmail().setText(companyDTO.getContactEmail());
                    companyView.getWebsite().setText(companyDTO.getWebsite());
                    companyView.setFullDescription(companyDTO.getFullDescription());
                    companyView.setIconUrl(companyDTO.getIconURL());
                }

            }).call(ServicesUtil.assetsService).getCompany();
        } catch (RequestException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(companyView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                companyDTO.setName(companyView.getName().getText());
                companyDTO.setDescription(companyView.getDescription().getText());
                companyDTO.setContactEmail(companyView.getEmail().getText());
                companyDTO.setWebsite(companyView.getWebsite().getText());
                companyDTO.setFullDescription(companyView.getFullDescription());
                companyDTO.setIconURL(companyView.getIconUrl());
                try {
                    displayLoading("Saving company...");
                    REST.withCallback(new MethodCallback<Void>() {

                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError("Error saving company...");
                        }

                        @Override
                        public void onSuccess(Method method, Void result) {
                            hideLoading();
                            displaySuccess("Company saved");
                        }

                    }).call(ServicesUtil.assetsService).updateCompany(companyDTO);
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

}
