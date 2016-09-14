package com.geocento.webapps.eobroker.common.shared.entities.orders;

import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import javax.persistence.*;
import java.util.Date;
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
    Product product;

    @Column(length = 10000)
    private String aoIWKT;

    @ElementCollection
    List<FormElementValue> formValues;

    @OneToMany(mappedBy = "productServiceRequest", cascade = CascadeType.ALL)
    List<ProductServiceSupplierRequest> supplierRequests;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

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

    public void setAoIWKT(String aoIWKT) {
        this.aoIWKT = aoIWKT;
    }

    public String getAoIWKT() {
        return aoIWKT;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
