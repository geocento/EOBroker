package com.geocento.webapps.eobroker.customer.shared.requests;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import java.util.List;

/**
 * Created by thomas on 06/07/2016.
 */
public class OTSProductRequestDTO {

    String id;
    Long productDatasetID;
    String aoIWKT;
    List<FormElementValue> formValues;

    String name;
    String comments;
    String selection;

    public OTSProductRequestDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getProductDatasetID() {
        return productDatasetID;
    }

    public void setProductDatasetID(Long productDatasetID) {
        this.productDatasetID = productDatasetID;
    }

    public String getAoIWKT() {
        return aoIWKT;
    }

    public void setAoIWKT(String aoIWKT) {
        this.aoIWKT = aoIWKT;
    }

    public List<FormElementValue> getFormValues() {
        return formValues;
    }

    public void setFormValues(List<FormElementValue> formValues) {
        this.formValues = formValues;
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
}
