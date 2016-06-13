package com.geocento.webapps.eobroker.common.shared.entities.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.User;

/**
 * Created by thomas on 13/06/2016.
 */
public class LoginInfo {

    private String userName;
    private User.USER_ROLE userRole;

    public LoginInfo() {
    }

    public LoginInfo(User user) {
        userName = user.getUsername();
        userRole = user.getRole();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User.USER_ROLE getUserRole() {
        return userRole;
    }

    public void setUserRole(User.USER_ROLE userRole) {
        this.userRole = userRole;
    }
}
