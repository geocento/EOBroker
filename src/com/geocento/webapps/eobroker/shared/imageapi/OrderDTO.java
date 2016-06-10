package com.geocento.webapps.eobroker.shared.imageapi;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by thomas on 03/03/2016.
 */
public class OrderDTO {

    private Date creationTime;
    private Date lastUpdate;
    private HashSet<String> eulas;

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setEulas(HashSet<String> eulas) {
        this.eulas = eulas;
    }

    public HashSet<String> getEulas() {
        return eulas;
    }

    static public enum STATUS {
        // user requested the products in the order
        REQUESTED,
        // supplier rejected the request
        CANCELLED,
        // supplier quoted the request
        QUOTED,
        // user rejected the quote
        QUOTEREJECTED,
        // user accepted and paid the quote
        ACCEPTED,
        // products are being generated
        INPRODUCTION,
        // failed to produce the order
        FAILED,
        // order has been completed and products are available
        COMPLETED,
        // order has been archived and products have been delivered and are not downloadable anymore
        DELIVERED
    };

    String id;
    STATUS status;
    String emailAddress;
    String name;
    String password;
    List<CommentDTO> comments;
    List<ProductOrderDTO> products;

    public OrderDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public List<ProductOrderDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductOrderDTO> products) {
        this.products = products;
    }
}
