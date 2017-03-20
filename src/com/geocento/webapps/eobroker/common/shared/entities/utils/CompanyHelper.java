package com.geocento.webapps.eobroker.common.shared.entities.utils;

import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

/**
 * Created by thomas on 08/06/2016.
 */
public class CompanyHelper {

    public static CompanyDTO createCompanyDTO(Company company) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(company.getId());
        companyDTO.setIconURL(company.getIconURL());
        companyDTO.setName(company.getName());
        companyDTO.setDescription(company.getDescription());
        companyDTO.setContactEmail(company.getContactEmail());
        companyDTO.setWebsite(company.getWebsite());
        return companyDTO;
    }

    public static CompanyDTO createFullCompanyDTO(Company company) {
        CompanyDTO companyDTO = createCompanyDTO(company);
        companyDTO.setFullDescription(company.getFullDescription());
        companyDTO.setAddress(company.getAddress());
        companyDTO.setCountryCode(company.getCountryCode());
        companyDTO.setAwards(company.getAwards());
        return companyDTO;
    }
}
