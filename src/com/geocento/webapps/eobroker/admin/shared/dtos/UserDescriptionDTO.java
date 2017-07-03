package com.geocento.webapps.eobroker.admin.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.REGISTRATION_STATUS;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

/**
 * Created by thomas on 11/07/2016.
 */
public class UserDescriptionDTO {

    String name;
    CompanyDTO companyDTO;
    private String password;
    private User.USER_ROLE userRole;
    private String email;
    private REGISTRATION_STATUS status;

    public UserDescriptionDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CompanyDTO getCompanyDTO() {
        return companyDTO;
    }

    public void setCompanyDTO(CompanyDTO companyDTO) {
        this.companyDTO = companyDTO;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User.USER_ROLE getUserRole() {
        return userRole;
    }

    public void setUserRole(User.USER_ROLE userRole) {
        this.userRole = userRole;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(REGISTRATION_STATUS status) {
        this.status = status;
    }

    public REGISTRATION_STATUS getStatus() {
        return status;
    }
}
