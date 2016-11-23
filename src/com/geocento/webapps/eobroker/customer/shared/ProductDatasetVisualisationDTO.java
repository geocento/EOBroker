package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductDatasetVisualisationDTO {

    Long id;
    CompanyDTO company;
    ProductDTO product;
    String imageUrl;
    String name;
    String description;
    List<DatasetAccess> datasetAccess;
    List<DatasetAccess> samples;

    public ProductDatasetVisualisationDTO() {
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

    public List<DatasetAccess> getDatasetAccess() {
        return datasetAccess;
    }

    public void setDatasetAccess(List<DatasetAccess> datasetAccess) {
        this.datasetAccess = datasetAccess;
    }

    public List<DatasetAccess> getSamples() {
        return samples;
    }

    public void setSamples(List<DatasetAccess> samples) {
        this.samples = samples;
    }
}
