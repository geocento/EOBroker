package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.datasets.DatasetStandard;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductDatasetDTO extends Offer {

    Long id;
    CompanyDTO company;
    ProductDTO product;
    String imageUrl;
    String name;
    String description;
    String fullDescription;
    String extent;
    String email;
    String website;
    String apiUrl;
    String sampleWmsUrl;
    DatasetStandard supportedCatalogueInterface;

    public ProductDatasetDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
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

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getSampleWmsUrl() {
        return sampleWmsUrl;
    }

    public void setSampleWmsUrl(String sampleWmsUrl) {
        this.sampleWmsUrl = sampleWmsUrl;
    }

    public DatasetStandard getSupportedCatalogueInterface() {
        return supportedCatalogueInterface;
    }

    public void setSupportedCatalogueInterface(DatasetStandard supportedCatalogueInterface) {
        this.supportedCatalogueInterface = supportedCatalogueInterface;
    }
}
