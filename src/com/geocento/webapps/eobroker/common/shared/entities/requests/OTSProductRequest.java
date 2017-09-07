package com.geocento.webapps.eobroker.common.shared.entities.requests;

import com.geocento.webapps.eobroker.common.shared.entities.Message;
import com.geocento.webapps.eobroker.common.shared.entities.ProductDataset;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 06/07/2016.
 */
@Entity
public class OTSProductRequest extends Request {

    @ManyToOne
    ProductDataset productDataset;

    // original request
    @Column(length = 10000)
    String aoIWKT;

    @ElementCollection
    List<FormElementValue> formValues;

    // the request selection and properties
    @Column
    String name;

    @Column(length = 1000)
    String comments;

    @Column(length = 10000)
    String selection;

    // supplier part
    @Column(length = 10000)
    String response;

    @OneToMany
    List<Message> messages;

    public OTSProductRequest() {
    }

    public void setFormValues(List<FormElementValue> formValues) {
        this.formValues = formValues;
    }

    public List<FormElementValue> getFormValues() {
        return formValues;
    }

    public void setAoIWKT(String aoIWKT) {
        this.aoIWKT = aoIWKT;
    }

    public String getAoIWKT() {
        return aoIWKT;
    }

    public ProductDataset getProductDataset() {
        return productDataset;
    }

    public void setProductDataset(ProductDataset productDataset) {
        this.productDataset = productDataset;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
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
}
