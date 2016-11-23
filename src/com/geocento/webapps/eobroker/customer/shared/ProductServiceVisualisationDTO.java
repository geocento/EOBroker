package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductServiceVisualisationDTO {

    Long id;
    String name;
    String description;
    String serviceImage;
    ProductDTO product;
    CompanyDTO company;
    List<DatasetAccess> samples;

    public ProductServiceVisualisationDTO() {
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

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public List<DatasetAccess> getSamples() {
        return samples;
    }

    public void setSamples(List<DatasetAccess> samples) {
        this.samples = samples;
    }
}
