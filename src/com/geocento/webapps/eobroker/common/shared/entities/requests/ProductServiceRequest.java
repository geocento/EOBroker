package com.geocento.webapps.eobroker.common.shared.entities.requests;

import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 06/07/2016.
 */
@Entity
public class ProductServiceRequest extends Request {

    @ManyToOne
    Product product;

    @Column(length = 10000)
    String aoIWKT;

    @ElementCollection
    List<FormElementValue> formValues;

    @OneToMany(mappedBy = "productServiceRequest", cascade = CascadeType.ALL)
    List<ProductServiceSupplierRequest> supplierRequests;

    // the feasibility search id from which the request originated, if not null
    @Column(length = 16)
    String searchId;

    public ProductServiceRequest() {
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

    public void setAoIWKT(String aoIWKT) {
        this.aoIWKT = aoIWKT;
    }

    public String getAoIWKT() {
        return aoIWKT;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getSearchId() {
        return searchId;
    }
}
