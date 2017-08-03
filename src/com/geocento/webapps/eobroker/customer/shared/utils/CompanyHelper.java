package com.geocento.webapps.eobroker.customer.shared.utils;

import com.geocento.webapps.eobroker.customer.shared.CreateCompanyDTO;

/**
 * Created by thomas on 03/08/2017.
 */
public class CompanyHelper {

/*
    public static String companyNameRegexp = "^([A-Za-z]|[0-9]|_)+$";
*/

    public static void checkCompanyValues(CreateCompanyDTO createCompanyDTO) throws Exception {
        if(createCompanyDTO.getName() == null || createCompanyDTO.getName().length() < 3) {
            throw new Exception("The company name is not valid");
        }

        if(createCompanyDTO.getAddress() == null || createCompanyDTO.getAddress().length() < 5) {
            throw new Exception("The company address is not valid");
        }

        if(createCompanyDTO.getCountryCode() == null || createCompanyDTO.getCountryCode().length() == 0) {
            throw new Exception("The company country is not valid");
        }

        if(createCompanyDTO.getDescription() == null || createCompanyDTO.getDescription().length() == 0) {
            throw new Exception("The company description is not valid");
        }
    }
}
