package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

/**
 * Created by thomas on 13/06/2016.
 */
public class LoginInfo extends com.geocento.webapps.eobroker.common.shared.entities.dtos.LoginInfo {

    CompanyDTO companyDTO;

    public LoginInfo() {
    }

    public LoginInfo(User user) {
        super(user);
        companyDTO = CompanyHelper.createCompanyDTO(user.getCompany());
    }

    public CompanyDTO getCompanyDTO() {
        return companyDTO;
    }

    public void setCompanyDTO(CompanyDTO companyDTO) {
        this.companyDTO = companyDTO;
    }
}
