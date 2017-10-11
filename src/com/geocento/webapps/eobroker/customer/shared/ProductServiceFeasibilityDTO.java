package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductServiceFeasibilityDTO {

    Long id;
    String name;
    String imageURL;
    String apiURL;
    CompanyDTO company;
    boolean hasSamples;

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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public boolean isHasSamples() {
        return hasSamples;
    }

    public void setHasSamples(boolean hasSamples) {
        this.hasSamples = hasSamples;
    }
}
