package com.geocento.webapps.eobroker.common.shared.imageapi;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * NOT SUPPORTED YET - the order content
 */
public class Order {

    /**
     * the status of an order
     */
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
    List<Comment> comments;
    List<ProductOrder> products;
    Date creationTime;
    Date lastUpdate;
    HashSet<String> eulas;

    public Order() {
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<ProductOrder> getProducts() {
        return products;
    }

    public void setProducts(List<ProductOrder> products) {
        this.products = products;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public HashSet<String> getEulas() {
        return eulas;
    }

    public void setEulas(HashSet<String> eulas) {
        this.eulas = eulas;
    }
}
