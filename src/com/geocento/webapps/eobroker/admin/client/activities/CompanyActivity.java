package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.CompanyPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.CompanyView;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.REGISTRATION_STATUS;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
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

    private CompanyDTO company;

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
        setTemplateView(companyView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        Long companyId = null;
        if(tokens.containsKey(CompanyPlace.TOKENS.id.toString())) {
            companyId = Long.parseLong(tokens.get(CompanyPlace.TOKENS.id.toString()));
        }
        if(companyId != null) {
            try {
                REST.withCallback(new MethodCallback<CompanyDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, CompanyDTO response) {
                        setCompany(response);
                    }
                }).call(ServicesUtil.assetsService).getCompany(companyId);
            } catch (RequestException e) {
            }
        } else {
            CompanyDTO company = new CompanyDTO();
            setCompany(company);
        }
    }

    private void setCompany(CompanyDTO company) {
        this.company = company;
        companyView.setTitleLine(company.getId() == null ? "Create company" : "Edit company");
        companyView.getName().setText(company.getName());
        companyView.getSupplier().setValue(company.isSupplier());
        companyView.getStatus().setValue(company.getStatus());
        boolean isValidated = company.getStatus() == REGISTRATION_STATUS.APPROVED;
        companyView.displayValidate(!isValidated);
        companyView.setIconUrl(company.getIconURL());
        companyView.getDescription().setText(company.getDescription());
        companyView.setFullDescription(company.getFullDescription());
        companyView.getWebsite().setText(company.getWebsite());
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(companyView.getValidate().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                try {
                    REST.withCallback(new MethodCallback<Void>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            companyView.setLoadingError("Error validating company, reason is " + method.getResponse().getText());
                        }

                        @Override
                        public void onSuccess(Method method, Void result) {
                            companyView.hideLoading("Company validated");
                            company.setStatus(REGISTRATION_STATUS.APPROVED);
                            setCompany(company);
                        }
                    }).call(ServicesUtil.assetsService).approveCompany(company.getId());
                } catch (RequestException e) {
                }
            }
        }));

        handlers.add(companyView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                company.setName(companyView.getName().getText());
                company.setSupplier(companyView.getSupplier().getValue());
                company.setStatus(companyView.getStatus().getValue());
                company.setIconURL(companyView.getIconUrl());
                company.setDescription(companyView.getDescription().getText());
                company.setFullDescription(companyView.getFullDescription());
                company.setWebsite(companyView.getWebsite().getText());
                companyView.setLoading("Saving company...");
                try {
                    REST.withCallback(new MethodCallback<Long>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            companyView.setLoadingError("Error saving company");
                        }

                        @Override
                        public void onSuccess(Method method, Long companyId) {
                            companyView.hideLoading("Company saved");
                            company.setId(companyId);
                        }
                    }).call(ServicesUtil.assetsService).saveCompany(company);
                } catch (RequestException e) {
                }
            }
        }));
    }

}
