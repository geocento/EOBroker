package com.geocento.webapps.eobroker.common.shared.entities.orders;

import com.geocento.webapps.eobroker.common.shared.entities.Product;
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

    @ElementCollection
    List<FormElementValue> formValues;

    @ManyToOne
    Product product;

    @OneToMany(mappedBy = "productServiceRequest", cascade = CascadeType.ALL)
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

    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
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
