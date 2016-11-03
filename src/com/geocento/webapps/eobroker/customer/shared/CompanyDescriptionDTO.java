package com.geocento.webapps.eobroker.customer.shared;

import java.util.Set;

/**
 * Created by thomas on 07/06/2016.
 */
public class CompanyDescriptionDTO {

    Long id;
    String name;
    String iconURL;
    String description;
    String contactEmail;
    String fullDescription;
    String website;
    private Set<ProductServiceDTO> productServices;

    public CompanyDescriptionDTO() {
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

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWebsite() {
        return website;
    }

    public void setProductServices(Set<ProductServiceDTO> productServices) {
        this.productServices = productServices;
    }

    public Set<ProductServiceDTO> getProductServices() {
        return productServices;
    }
}
