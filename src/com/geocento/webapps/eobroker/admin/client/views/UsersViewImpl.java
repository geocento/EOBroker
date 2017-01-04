package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.widgets.CompanyTextBox;
import com.geocento.webapps.eobroker.admin.client.widgets.UsersList;
import com.geocento.webapps.eobroker.admin.shared.dtos.UserDescriptionDTO;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingCellTable;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class UsersViewImpl extends Composite implements UsersView {

    interface UsersViewUiBinder extends UiBinder<Widget, UsersViewImpl> {
    }

    private static UsersViewUiBinder ourUiBinder = GWT.create(UsersViewUiBinder.class);

    public static interface Style extends CssResource {
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    UsersList users;
    @UiField
    MaterialButton createNew;
    @UiField
    MaterialTextBox filter;
    @UiField
    MaterialModal editUser;
    @UiField
    MaterialTextBox userName;
    @UiField
    MaterialTextBox email;
    @UiField
    MaterialListBox role;
    @UiField
    CompanyTextBox company;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialButton cancel;
    @UiField
    MaterialTitle title;

    private Presenter presenter;

    public UsersViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        users.setPresenter(new AsyncPagingCellTable.Presenter() {
            @Override
            public void rangeChanged(int start, int length, Column<?, ?> column, boolean isAscending) {
                presenter.rangeChanged(start, length);
            }
        });

        filter.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.changeFilter(event.getValue());
            }
        });

        // set the user role values
        for(User.USER_ROLE role : User.USER_ROLE.values()) {
            this.role.addItem(role.toString());
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HasClickHandlers getCreateNewButton() {
        return createNew;
    }

    @Override
    public void clearUsers() {
        this.users.clearData();
    }

    @Override
    public void setUsersLoading(boolean loading) {
        users.setLoading(loading);
    }

    @Override
    public void setUsers(int start, List<UserDescriptionDTO> userDTOs) {
        this.users.setRowData(start, userDTOs);
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void editUser(UserDescriptionDTO userDescriptionDTO) {
        title.setTitle(userDescriptionDTO.getName() == null ? "Create new user" : "Edit user settings");
        userName.setText(userDescriptionDTO.getName());
        email.setText(userDescriptionDTO.getEmail());
        role.setSelectedValue(userDescriptionDTO.getUserRole().toString());
        company.setCompany(userDescriptionDTO.getCompanyDTO());
        editUser.openModal();
    }

    @UiHandler("cancel")
    public void cancel(ClickEvent clickEvent) {
        editUser.closeModal();
    }

    @Override
    public HasClickHandlers getSaveUserButton() {
        return submit;
    }

    @Override
    public String getUserName() {
        return userName.getText();
    }

    @Override
    public String getUserEmail() {
        return email.getText();
    }

    @Override
    public User.USER_ROLE getUserRole() {
        return User.USER_ROLE.valueOf(role.getSelectedValue());
    }

    @Override
    public CompanyDTO getUserCompany() {
        return company.getCompany();
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}