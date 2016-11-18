package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductServiceDescriptionDTO {

    Long id;
    String name;
    String description;
    String serviceImage;
    ProductDTO product;
    String email;
    String website;
    String fullDescription;
    CompanyDTO company;
    boolean hasFeasibility;
    List<ProductServiceDTO> suggestedServices;
    private List<FeatureDescription> geoinformation;
    private String extent;

    public ProductServiceDescriptionDTO() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWebsite() {
        return website;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public boolean isHasFeasibility() {
        return hasFeasibility;
    }

    public void setHasFeasibility(boolean hasFeasibility) {
        this.hasFeasibility = hasFeasibility;
    }

    public List<ProductServiceDTO> getSuggestedServices() {
        return suggestedServices;
    }

    public void setSuggestedServices(List<ProductServiceDTO> suggestedServices) {
        this.suggestedServices = suggestedServices;
    }

    public List<FeatureDescription> getGeoinformation() {
        return geoinformation;
    }

    public void setGeoinformation(List<FeatureDescription> geoinformation) {
        this.geoinformation = geoinformation;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public String getExtent() {
        return extent;
    }
}
