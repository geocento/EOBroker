package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.COMPANY_SIZE;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

import java.util.List;

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
    String address;
    String countryCode;
    COMPANY_SIZE companySize;
    boolean following;
    int followers;
    List<String> awards;
    List<ProductServiceDTO> productServices;
    List<ProductDatasetDTO> productDatasets;
    List<SoftwareDTO> software;
    List<ProjectDTO> project;
    private List<CompanyDTO> suggestedCompanies;
    private List<TestimonialDTO> testimonials;

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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public List<String> getAwards() {
        return awards;
    }

    public void setAwards(List<String> awards) {
        this.awards = awards;
    }

    public List<ProductServiceDTO> getProductServices() {
        return productServices;
    }

    public void setProductServices(List<ProductServiceDTO> productServices) {
        this.productServices = productServices;
    }

    public List<ProductDatasetDTO> getProductDatasets() {
        return productDatasets;
    }

    public void setProductDatasets(List<ProductDatasetDTO> productDatasets) {
        this.productDatasets = productDatasets;
    }

    public List<SoftwareDTO> getSoftware() {
        return software;
    }

    public void setSoftware(List<SoftwareDTO> software) {
        this.software = software;
    }

    public List<ProjectDTO> getProject() {
        return project;
    }

    public void setProject(List<ProjectDTO> project) {
        this.project = project;
    }

    public List<CompanyDTO> getSuggestedCompanies() {
        return suggestedCompanies;
    }

    public void setSuggestedCompanies(List<CompanyDTO> suggestedCompanies) {
        this.suggestedCompanies = suggestedCompanies;
    }

    public void setTestimonials(List<TestimonialDTO> testimonials) {
        this.testimonials = testimonials;
    }

    public List<TestimonialDTO> getTestimonials() {
        return testimonials;
    }
}
