package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.RequestAccessPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.RequestAccessView;
import com.geocento.webapps.eobroker.customer.shared.CreateCompanyDTO;
import com.geocento.webapps.eobroker.customer.shared.CreateUserDTO;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

/**
 * Created by thomas on 09/05/2016.
 */
public class RequestAccessActivity extends AbstractApplicationActivity implements RequestAccessView.Presenter {

    private RequestAccessView requestAccessView;

    public RequestAccessActivity(RequestAccessPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        requestAccessView = clientFactory.getRequestAccessView();
        requestAccessView.setPresenter(this);
        panel.setWidget(requestAccessView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
    }

    @Override
    protected void bind() {
        handlers.add(requestAccessView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // create new user
                // first check if an existing company has been selected
                CompanyDTO companyDTO = requestAccessView.getCompany();
                if(companyDTO == null) {
                    // create new company
                    CreateCompanyDTO createCompanyDTO = new CreateCompanyDTO();
                    createCompanyDTO.setName(requestAccessView.getCompanyName().getText());
                    createCompanyDTO.setDescription(requestAccessView.getCompanyDescription().getText());
                    createCompanyDTO.setAddress(requestAccessView.getCompanyAddress().getText());
                    createCompanyDTO.setCountryCode(requestAccessView.getCompanyCountryCode());
                    // save company
                    try {
                        requestAccessView.displayLoading("Creating company...");
                        REST.withCallback(new MethodCallback<CompanyDTO>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                requestAccessView.hideLoading();
                                requestAccessView.displayCompanyCreationError("Company could not be created, reason is " + exception.getMessage());
                            }

                            @Override
                            public void onSuccess(Method method, CompanyDTO companyDTO) {
                                requestAccessView.hideLoading();
                                createUser(companyDTO);
                            }
                        }).call(ServicesUtil.assetsService).createCompany(createCompanyDTO);
                    } catch (Exception e) {
                    }
                } else {
                    createUser(companyDTO);
                }
            }

            private void createUser(CompanyDTO companyDTO) {
                // save user
                String userName = requestAccessView.getUserName().getText();
                String userPassword = requestAccessView.getPassword().getText();
                String fullName = requestAccessView.getFullName().getText();
                String email = requestAccessView.getEmail().getText();
                String phoneNumber = requestAccessView.getPhoneNumber().getText();
                CreateUserDTO createUserDTO = new CreateUserDTO();
                createUserDTO.setUserName(userName);
                createUserDTO.setUserPassword(userPassword);
                createUserDTO.setEmail(email);
                createUserDTO.setFullName(fullName);
                createUserDTO.setPhoneNumber(phoneNumber);
                createUserDTO.setCompanyId(companyDTO.getId());
                try {
                    requestAccessView.displayLoading("Saving user");
                    REST.withCallback(new MethodCallback<Void>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            requestAccessView.hideLoading();
                            requestAccessView.displayUserCreationError("User could not be created, reason is " + exception.getMessage());
                        }

                        @Override
                        public void onSuccess(Method method, Void result) {
                            requestAccessView.hideLoading();
                            requestAccessView.displayCreationSuccess("Your user has been successfully created. " +
                                    "Our staff will contact you very shortly to validate your request and provide you access to the EO Broker.");
                        }
                    }).call(ServicesUtil.assetsService).createUser(createUserDTO);
                } catch (Exception e) {
                }
            }

        }));

    }

}
