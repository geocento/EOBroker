package com.geocento.webapps.eobroker.common.shared.imageapi;

import java.util.List;

/**
 * Created by thomas on 03/03/2016.
 */
public class SubmitOrder {

    String emailAddress;
    String name;
    String password;
    List<ProductRequestDTO> products;
    String comment;

    public SubmitOrder() {
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

    public List<ProductRequestDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductRequestDTO> products) {
        this.products = products;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
