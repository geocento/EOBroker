package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

/**
 * Created by thomas on 11/07/2016.
 */
public class CompanyHelper {
    public static CompanyDTO createCompanyDTO(Company company) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(company.getId());
        companyDTO.setName(company.getName());
        companyDTO.setDescription(company.getDescription());
        companyDTO.setFullDescription(company.getFullDescription());
        companyDTO.setIconURL(company.getIconURL());
        companyDTO.setContactEmail(company.getContactEmail());
        companyDTO.setWebsite(company.getWebsite());
        return companyDTO;
    }
}
