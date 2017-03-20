package com.geocento.webapps.eobroker.common.shared.entities.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.COMPANY_SIZE;

import java.util.List;

/**
 * Created by thomas on 07/06/2016.
 */
public class CompanyDTO {

    Long id;
    String name;
    String iconURL;
    String description;
    String contactEmail;
    String fullDescription;
    String website;
    String address;
    String countryCode;
    COMPANY_SIZE companySize;
    List<String> awards;

    public CompanyDTO() {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public COMPANY_SIZE getCompanySize() {
        return companySize;
    }

    public void setCompanySize(COMPANY_SIZE companySize) {
        this.companySize = companySize;
    }

    public List<String> getAwards() {
        return awards;
    }

    public void setAwards(List<String> awards) {
        this.awards = awards;
    }
}
