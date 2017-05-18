package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.datasets.DatasetStandard;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductDatasetCatalogueDTO {

    Long id;
    CompanyDTO company;
    ProductDTO product;
    String imageUrl;
    String name;
    String description;
    String extent;
    // catalogue values
    DatasetStandard datasetStandard;
    String datasetURL;
    String catalogueDescription;
    List<FormElement> queriableFields;

    public ProductDatasetCatalogueDTO() {
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

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public DatasetStandard getDatasetStandard() {
        return datasetStandard;
    }

    public void setDatasetStandard(DatasetStandard datasetStandard) {
        this.datasetStandard = datasetStandard;
    }

    public String getDatasetURL() {
        return datasetURL;
    }

    public void setDatasetURL(String datasetURL) {
        this.datasetURL = datasetURL;
    }

    public String getCatalogueDescription() {
        return catalogueDescription;
    }

    public void setCatalogueDescription(String catalogueDescription) {
        this.catalogueDescription = catalogueDescription;
    }

    public List<FormElement> getQueriableFields() {
        return queriableFields;
    }

    public void setQueriableFields(List<FormElement> queriableFields) {
        this.queriableFields = queriableFields;
    }
}
