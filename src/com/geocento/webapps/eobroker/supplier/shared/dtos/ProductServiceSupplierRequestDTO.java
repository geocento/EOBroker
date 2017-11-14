package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 11/07/2016.
 */
public class ProductServiceSupplierRequestDTO extends BaseRequestDTO {

    ProductDTO product;
    List<FormElementValue> formValues;
    private String supplierResponse;
    private String aoIWKT;
    private String serviceName;
    private Date creationTime;
    private String serviceImage;
    private Long supplierRequestId;
    private String searchId;

    public ProductServiceSupplierRequestDTO() {
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public List<FormElementValue> getFormValues() {
        return formValues;
    }

    public void setFormValues(List<FormElementValue> formValues) {
        this.formValues = formValues;
    }

    public void setSupplierResponse(String supplierResponse) {
        this.supplierResponse = supplierResponse;
    }

    public String getSupplierResponse() {
        return supplierResponse;
    }

    public void setAoIWKT(String aoIWKT) {
        this.aoIWKT = aoIWKT;
    }

    public String getAoIWKT() {
        return aoIWKT;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public void setSupplierRequestId(Long supplierRequestId) {
        this.supplierRequestId = supplierRequestId;
    }

    public Long getSupplierRequestId() {
        return supplierRequestId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getSearchId() {
        return searchId;
    }
}
