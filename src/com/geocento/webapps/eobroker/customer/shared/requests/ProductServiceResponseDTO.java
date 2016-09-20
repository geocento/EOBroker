package com.geocento.webapps.eobroker.customer.shared.requests;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import java.util.List;

/**
 * Created by thomas on 11/07/2016.
 */
public class ProductServiceResponseDTO {

    String id;
    ProductDTO product;
    List<FormElementValue> formValues;
    String aoIWKT;
    List<ProductServiceSupplierResponseDTO> supplierResponses;

    public ProductServiceResponseDTO() {
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

    public List<ProductServiceSupplierResponseDTO> getSupplierResponses() {
        return supplierResponses;
    }

    public void setSupplierResponses(List<ProductServiceSupplierResponseDTO> supplierResponses) {
        this.supplierResponses = supplierResponses;
    }

    public void setAoIWKT(String aoIWKT) {
        this.aoIWKT = aoIWKT;
    }

    public String getAoIWKT() {
        return aoIWKT;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
