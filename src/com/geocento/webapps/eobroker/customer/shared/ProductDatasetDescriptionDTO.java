package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.FeatureDescription;
import com.geocento.webapps.eobroker.common.shared.entities.PerformanceValue;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductDatasetDescriptionDTO {

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
    boolean commercial;
    List<FeatureDescription> geoinformation;
    String geoinformationComment;
    List<PerformanceValue> performances;
    String performancesComments;
    List<DatasetAccess> datasetAccesses;
    List<DatasetAccess> samples;
    List<ProductDatasetDTO> suggestedDatasets;

    public ProductDatasetDescriptionDTO() {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public boolean isCommercial() {
        return commercial;
    }

    public void setCommercial(boolean commercial) {
        this.commercial = commercial;
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

    public void setGeoinformation(List<FeatureDescription> geoinformation) {
        this.geoinformation = geoinformation;
    }

    public List<FeatureDescription> getGeoinformation() {
        return geoinformation;
    }

    public String getGeoinformationComment() {
        return geoinformationComment;
    }

    public void setGeoinformationComment(String geoinformationComment) {
        this.geoinformationComment = geoinformationComment;
    }

    public List<PerformanceValue> getPerformances() {
        return performances;
    }

    public void setPerformances(List<PerformanceValue> performances) {
        this.performances = performances;
    }

    public String getPerformancesComments() {
        return performancesComments;
    }

    public void setPerformancesComments(String performancesComments) {
        this.performancesComments = performancesComments;
    }

    public List<ProductDatasetDTO> getSuggestedDatasets() {
        return suggestedDatasets;
    }

    public void setSuggestedDatasets(List<ProductDatasetDTO> suggestedDatasets) {
        this.suggestedDatasets = suggestedDatasets;
    }

}
