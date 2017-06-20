package com.geocento.webapps.eobroker.common.shared.feasibility;

/**
 * Created by thomas on 15/06/2017.
 */
public class UserInformation {

    String userName;
    String userCompany;

    public UserInformation() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCompany() {
        return userCompany;
    }

    public void setUserCompany(String userCompany) {
        this.userCompany = userCompany;
    }
}
