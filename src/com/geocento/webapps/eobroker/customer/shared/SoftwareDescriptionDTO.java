package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class SoftwareDescriptionDTO {

    Long id;
    String name;
    String description;
    String imageUrl;
    String email;
    String website;
    String fullDescription;
    CompanyDTO companyDTO;
    List<ProductSoftwareDTO> products;
    private boolean commercial;
    private boolean openSource;
    private List<SoftwareDTO> suggestedSoftware;

    public SoftwareDescriptionDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ProductSoftwareDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductSoftwareDTO> products) {
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public CompanyDTO getCompanyDTO() {
        return companyDTO;
    }

    public void setCompanyDTO(CompanyDTO companyDTO) {
        this.companyDTO = companyDTO;
    }

    public boolean isCommercial() {
        return commercial;
    }

    public void setCommercial(boolean commercial) {
        this.commercial = commercial;
    }

    public boolean isOpenSource() {
        return openSource;
    }

    public void setOpenSource(boolean openSource) {
        this.openSource = openSource;
    }

    public List<SoftwareDTO> getSuggestedSoftware() {
        return suggestedSoftware;
    }

    public void setSuggestedSoftware(List<SoftwareDTO> suggestedSoftware) {
        this.suggestedSoftware = suggestedSoftware;
    }
}
