package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.events.EditUser;
import com.geocento.webapps.eobroker.admin.client.events.EditUserHandler;
import com.geocento.webapps.eobroker.admin.client.places.UsersPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.UsersView;
import com.geocento.webapps.eobroker.admin.shared.dtos.UserDescriptionDTO;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.User;
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
public class UsersActivity extends TemplateActivity implements UsersView.Presenter {

    public static String emailregExp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
            +"[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
            +"(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

    private int start = 0;
    private int limit = 10;
    private String orderby = "";
    private String filter;

    private UsersView usersView;

    private UserDescriptionDTO userDescriptionDTO;

    public UsersActivity(UsersPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        usersView = clientFactory.getUsersView();
        usersView.setPresenter(this);
        panel.setWidget(usersView.asWidget());
        setTemplateView(usersView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        // load users
        loadUsers();
    }

    @Override
    protected void bind() {
        super.bind();

        activityEventBus.addHandler(EditUser.TYPE, new EditUserHandler() {
            @Override
            public void onEditUser(EditUser event) {
                userDescriptionDTO = event.getUserDescriptionDTO();
                usersView.editUser(userDescriptionDTO);
            }
        });

        handlers.add(usersView.getCreateNewButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                userDescriptionDTO = new UserDescriptionDTO();
                userDescriptionDTO.setUserRole(User.USER_ROLE.customer);
                usersView.editUser(userDescriptionDTO);
            }
        }));

        handlers.add(usersView.getSaveUserButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                userDescriptionDTO.setName(usersView.getUserName());
                userDescriptionDTO.setEmail(usersView.getUserEmail());
                userDescriptionDTO.setUserRole(usersView.getUserRole());
                userDescriptionDTO.setCompanyDTO(usersView.getUserCompany());
                try {
                    if(userDescriptionDTO.getName().length() < 5) {
                        throw new Exception("Not a valid name");
                    }
                    if(!userDescriptionDTO.getEmail().matches(emailregExp)) {
                        throw new Exception("Not a valid email");
                    }
                } catch (Exception e) {
                    usersView.displayError(e.getMessage());
                    return;
                }
                try {
                    usersView.hideEditUser();
                    usersView.displayLoading("Saving user...");
                    REST.withCallback(new MethodCallback<Void>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            Window.alert("Error loading users");
                            usersView.setUsersLoading(false);
                        }

                        @Override
                        public void onSuccess(Method method, Void response) {
                            usersView.hideLoading();
                            loadUsers();
                        }
                    }).call(ServicesUtil.assetsService).saveUser(userDescriptionDTO);
                } catch (RequestException e) {
                }
            }
        }));

        handlers.add(usersView.getCreateUserButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                userDescriptionDTO.setName(usersView.getUserName());
                userDescriptionDTO.setEmail(usersView.getUserEmail());
                userDescriptionDTO.setUserRole(usersView.getUserRole());
                userDescriptionDTO.setCompanyDTO(usersView.getUserCompany());
                try {
                    if(userDescriptionDTO.getName().length() < 5) {
                        throw new Exception("Not a valid name");
                    }
                    if(!userDescriptionDTO.getEmail().matches(emailregExp)) {
                        throw new Exception("Not a valid email");
                    }
                } catch (Exception e) {
                    usersView.displayError(e.getMessage());
                    return;
                }
                try {
                    usersView.hideEditUser();
                    usersView.displayLoading("Saving user...");
                    REST.withCallback(new MethodCallback<Void>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            Window.alert("Error loading users");
                            usersView.setUsersLoading(false);
                        }

                        @Override
                        public void onSuccess(Method method, Void response) {
                            usersView.hideLoading();
                            loadUsers();
                        }
                    }).call(ServicesUtil.assetsService).createUser(userDescriptionDTO);
                } catch (RequestException e) {
                }
            }
        }));
    }

    private void loadUsers() {
        try {
            usersView.setUsersLoading(true);
            REST.withCallback(new MethodCallback<List<UserDescriptionDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Error loading users");
                    usersView.setUsersLoading(false);
                }

                @Override
                public void onSuccess(Method method, List<UserDescriptionDTO> response) {
                    usersView.setUsersLoading(false);
                    usersView.setUsers(start, response);
                }
            }).call(ServicesUtil.assetsService).listUsers(start, limit, orderby, filter);
        } catch (RequestException e) {
        }
    }

    @Override
    public void rangeChanged(int start, int limit) {
        this.start = start;
        this.limit = limit;
        loadUsers();
    }

    @Override
    public void changeFilter(String value) {
        start = 0;
        filter = value;
        loadUsers();
    }

}
