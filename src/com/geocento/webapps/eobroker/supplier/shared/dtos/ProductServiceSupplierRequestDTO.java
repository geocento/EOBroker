package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import java.util.List;

/**
 * Created by thomas on 11/07/2016.
 */
public class ProductServiceSupplierRequestDTO {

    String id;
    UserDTO customer;
    ProductDTO product;
    List<FormElementValue> formValues;
    private String supplierResponse;

    public ProductServiceSupplierRequestDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserDTO getCustomer() {
        return customer;
    }

    public void setCustomer(UserDTO customer) {
        this.customer = customer;
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
}
