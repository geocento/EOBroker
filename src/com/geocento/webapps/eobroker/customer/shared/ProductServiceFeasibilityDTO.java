package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductServiceFeasibilityDTO {

    Long id;
    String name;
    String apiURL;
    CompanyDTO companyDTO;

    public ProductServiceFeasibilityDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setCompany(CompanyDTO companyDTO) {
        this.companyDTO = companyDTO;
    }

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }
}
