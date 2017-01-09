package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.shared.dtos.UserDescriptionDTO;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface UsersView extends IsWidget {

    void setPresenter(Presenter presenter);

    HasClickHandlers getCreateNewButton();

    void clearUsers();

    void setUsersLoading(boolean loading);

    void setUsers(int start, List<UserDescriptionDTO> userDTOs);

    TemplateView getTemplateView();

    void editUser(UserDescriptionDTO userDescriptionDTO);

    HasClickHandlers getSaveUserButton();

    String getUserName();

    String getUserEmail();

    User.USER_ROLE getUserRole();

    CompanyDTO getUserCompany();

    void displayError(String message);

    void hideEditUser();

    void displayLoading(String message);

    HasClickHandlers getCreateUserButton();

    void hideLoading();

    public interface Presenter {

        void rangeChanged(int start, int limit);

        void changeFilter(String value);
    }

}
