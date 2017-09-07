package com.geocento.webapps.eobroker.common.shared.entities.requests;

import com.geocento.webapps.eobroker.common.shared.entities.Message;
import com.geocento.webapps.eobroker.common.shared.entities.ProductService;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 06/07/2016.
 */
@Entity
public class ProductServiceSupplierRequest {

    @Id
    @GeneratedValue
    Long id;

    // TODO - add a status for archiving the request and storing the information, eg in case the service is removed...
    @Enumerated(EnumType.STRING)
    Request.STATUS status;

    @ManyToOne
    ProductService productService;

    @ManyToOne
    ProductServiceRequest productServiceRequest;

    @Column(length = 10000)
    String response;

    @OneToMany
    List<Message> messages;

    @Temporal(TemporalType.TIMESTAMP)
    Date lastModifiedDate;

    public ProductServiceSupplierRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Request.STATUS getStatus() {
        return status;
    }

    public void setStatus(Request.STATUS status) {
        this.status = status;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public ProductServiceRequest getProductServiceRequest() {
        return productServiceRequest;
    }

    public void setProductServiceRequest(ProductServiceRequest productServiceRequest) {
        this.productServiceRequest = productServiceRequest;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
