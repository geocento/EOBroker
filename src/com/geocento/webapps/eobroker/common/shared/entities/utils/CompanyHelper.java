package com.geocento.webapps.eobroker.common.shared.entities.utils;

import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

/**
 * Created by thomas on 08/06/2016.
 */
public class CompanyHelper {

    public static void populateCompanyDTO(CompanyDTO companyDTO, Company company, boolean full) {
        companyDTO.setId(company.getId());
        companyDTO.setIconURL(company.getIconURL());
        companyDTO.setName(company.getName());
        companyDTO.setDescription(company.getDescription());
        companyDTO.setContactEmail(company.getContactEmail());
        companyDTO.setWebsite(company.getWebsite());
        companyDTO.setFollowers(company.getFollowers() == null ? 0 : company.getFollowers().intValue());
        companyDTO.setSupplier(company.isSupplier());
        if(full) {
            companyDTO.setFullDescription(company.getFullDescription());
            companyDTO.setStartedIn(company.getStartedIn());
            companyDTO.setAddress(company.getAddress());
            companyDTO.setCountryCode(company.getCountryCode());
            companyDTO.setAwards(company.getAwards());
        }
    }

    public static CompanyDTO createCompanyDTO(Company company) {
        if(company == null) {
            return null;
        }
        CompanyDTO companyDTO = new CompanyDTO();
        populateCompanyDTO(companyDTO, company, false);
        return companyDTO;
    }

    public static CompanyDTO createFullCompanyDTO(Company company) {
        if(company == null) {
            return null;
        }
        CompanyDTO companyDTO = new CompanyDTO();
        populateCompanyDTO(companyDTO, company, true);
        return companyDTO;
    }

}
