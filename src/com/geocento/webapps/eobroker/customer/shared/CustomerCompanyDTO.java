package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

import java.util.List;

/**
 * Created by thomas on 06/09/2017.
 */
public class CustomerCompanyDTO extends CompanyDTO {

    List<UserDTO> users;
    List<CompanyDTO> affiliates;

    public CustomerCompanyDTO() {
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    public List<CompanyDTO> getAffiliates() {
        return affiliates;
    }

    public void setAffiliates(List<CompanyDTO> affiliates) {
        this.affiliates = affiliates;
    }
}
