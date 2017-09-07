package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 06/07/2016.
 */
public class OTSProductRequestDTO extends BaseRequestDTO {

    ProductDatasetDTO productDataset;
    
    String aoIWKT;
    List<FormElementValue> formValues;

    UserDTO userDTO;
    String name;
    String comments;
    String selection;
    private Date creationTime;
    private String supplierResponse;

    public OTSProductRequestDTO() {
    }

    public ProductDatasetDTO getProductDataset() {
        return productDataset;
    }

    public void setProductDataset(ProductDatasetDTO productDataset) {
        this.productDataset = productDataset;
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

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
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

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public String getSupplierResponse() {
        return supplierResponse;
    }

    public void setSupplierResponse(String supplierResponse) {
        this.supplierResponse = supplierResponse;
    }
}
