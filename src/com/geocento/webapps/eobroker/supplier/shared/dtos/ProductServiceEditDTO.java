package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.*;

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
    List<DatasetAccessOGC> coverageLayers;
    List<FeatureDescription> productFeatures;
    List<Long> selectedFeatures;
    String apiURL;
    List<DatasetAccess> samples;
    List<AccessType> selectedDataAccessTypes;
    List<PerformanceDescription> performances;
    List<PerformanceValue> providedPerformances;
    String performancesComment;
    String geoinformationComment;
    String disseminationComment;
    String timeToDelivery;

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

    public void setPerformances(List<PerformanceDescription> performances) {
        this.performances = performances;
    }

    public List<PerformanceDescription> getPerformances() {
        return performances;
    }

    public void setProvidedPerformances(List<PerformanceValue> providedPerformances) {
        this.providedPerformances = providedPerformances;
    }

    public List<PerformanceValue> getProvidedPerformances() {
        return providedPerformances;
    }

    public void setPerformancesComment(String performancesComment) {
        this.performancesComment = performancesComment;
    }

    public String getPerformancesComment() {
        return performancesComment;
    }

    public void setGeoinformationComment(String geoinformationComment) {
        this.geoinformationComment = geoinformationComment;
    }

    public String getGeoinformationComment() {
        return geoinformationComment;
    }

    public String getDisseminationComment() {
        return disseminationComment;
    }

    public void setDisseminationComment(String disseminationComment) {
        this.disseminationComment = disseminationComment;
    }

    public String getTimeToDelivery() {
        return timeToDelivery;
    }

    public void setTimeToDelivery(String timeToDelivery) {
        this.timeToDelivery = timeToDelivery;
    }

    public List<DatasetAccessOGC> getCoverageLayers() {
        return coverageLayers;
    }

    public void setCoverageLayers(List<DatasetAccessOGC> coverageLayers) {
        this.coverageLayers = coverageLayers;
    }
}
