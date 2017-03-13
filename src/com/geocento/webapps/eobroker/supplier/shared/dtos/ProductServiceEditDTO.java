package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.AccessType;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductServiceEditDTO {

    Long id;
    String name;
    String description;
    String serviceImage;
    ProductDTO product;
    String email;
    String website;
    String fullDescription;
    String extent;
    List<FeatureDescription> productFeatures;
    List<Long> selectedFeatures;
    String apiURL;
    private List<DatasetAccess> samples;
    private List<AccessType> selectedDataAccessTypes;

    public ProductServiceEditDTO() {
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

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
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

    public String getApiURL() {
        return apiURL;
    }

    public void setApiURL(String apiURL) {
        this.apiURL = apiURL;
    }

    public void setSamples(List<DatasetAccess> samples) {
        this.samples = samples;
    }

    public List<DatasetAccess> getSamples() {
        return samples;
    }

    public List<AccessType> getSelectedDataAccessTypes() {
        return selectedDataAccessTypes;
    }

    public void setSelectedDataAccessTypes(List<AccessType> selectedDataAccessTypes) {
        this.selectedDataAccessTypes = selectedDataAccessTypes;
    }
}
