package com.geocento.webapps.eobroker.common.shared.entities.orders;

import com.geocento.webapps.eobroker.common.shared.entities.ProductService;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 06/07/2016.
 */
@Entity
public class ProductServiceRequest {

    @Id
    String id;

    @ManyToOne
    User customer;

    @ManyToOne
    ProductService productService;

    @ElementCollection
    List<FormElementValue> formValues;

    @OneToMany(mappedBy = "productServiceRequest")
    List<ProductServiceSupplierRequest> supplierRequests;

    public ProductServiceRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String requestId) {
        this.id = requestId;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getCustomer() {
        return customer;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setFormValues(List<FormElementValue> formValues) {
        this.formValues = formValues;
    }

    public List<FormElementValue> getFormValues() {
        return formValues;
    }

    public List<ProductServiceSupplierRequest> getSupplierRequests() {
        return supplierRequests;
    }

    public void setSupplierRequests(List<ProductServiceSupplierRequest> supplierRequests) {
        this.supplierRequests = supplierRequests;
    }
}
