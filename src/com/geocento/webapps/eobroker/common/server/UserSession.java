package com.geocento.webapps.eobroker.common.server;

import com.geocento.webapps.eobroker.common.shared.entities.User;

import java.io.Serializable;

/**
 * Created by thomas on 13/06/2016.
 */
public class UserSession implements Serializable {

    private String userName;
    private User.USER_ROLE userRole;
    private String userCompanyName;
    private Long userCompanyId;

    public UserSession(User user) {
        userName = user.getUsername();
        userRole = user.getRole();
        if(user.getCompany() != null) {
            userCompanyId = user.getCompany().getId();
            userCompanyName =  user.getCompany().getName();
        }
    }

    public String getUserName() {
        return userName;
    }

    public User.USER_ROLE getUserRole() {
        return userRole;
    }

    public void setUserRole(User.USER_ROLE userRole) {
        this.userRole = userRole;
    }

    public String getUserCompanyName() {
        return userCompanyName;
    }

    public Long getUserCompanyId() {
        return userCompanyId;
    }
}
