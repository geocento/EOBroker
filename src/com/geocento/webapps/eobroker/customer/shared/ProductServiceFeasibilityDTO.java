package com.geocento.webapps.eobroker.customer.shared;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductServiceFeasibilityDTO {

    Long id;
    String name;
    String companyName;
    String apiURL;

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }
}
