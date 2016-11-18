package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;
import com.geocento.webapps.eobroker.common.shared.entities.ServiceType;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductDatasetDTO {

    Long id;
    CompanyDTO company;
    ProductDTO product;
    String name;
    String imageUrl;
    String description;
    String fullDescription;
    String extent;
    String website;
    ServiceType serviceType;
    List<DatasetAccess> datasetAccesses;
    List<DatasetAccess> samples;
    List<FeatureDescription> productFeatures;
    List<Long> selectedFeatures;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public List<DatasetAccess> getDatasetAccesses() {
        return datasetAccesses;
    }

    public void setDatasetAccesses(List<DatasetAccess> datasetAccesses) {
        this.datasetAccesses = datasetAccesses;
    }

    public List<DatasetAccess> getSamples() {
        return samples;
    }

    public void setSamples(List<DatasetAccess> samples) {
        this.samples = samples;
    }

    public List<FeatureDescription> getProductFeatures() {
        return productFeatures;
    }

    public void setProductFeatures(List<FeatureDescription> productFeatures) {
        this.productFeatures = productFeatures;
    }

    public List<Long> getSelectedFeatures() {
        return selectedFeatures;
    }

    public void setSelectedFeatures(List<Long> selectedFeatures) {
        this.selectedFeatures = selectedFeatures;
    }
}
