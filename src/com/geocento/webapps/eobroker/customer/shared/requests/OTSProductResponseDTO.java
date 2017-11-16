package com.geocento.webapps.eobroker.customer.shared.requests;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.entities.requests.Request;
import com.geocento.webapps.eobroker.customer.shared.ProductDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductDatasetDTO;
import com.geocento.webapps.eobroker.customer.shared.UserDTO;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 11/07/2016.
 */
public class OTSProductResponseDTO {

    String id;
    ProductDTO product;

    ProductDatasetDTO productDataset;

    String aoIWKT;
    List<FormElementValue> formValues;

    UserDTO userDTO;
    String name;
    String comments;
    String selection;
    Date creationTime;

    Request.STATUS status;

    // supplier response
    String response;
    Date responseDate;
    List<MessageDTO> messages;

    public OTSProductResponseDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
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

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Request.STATUS getStatus() {
        return status;
    }

    public void setStatus(Request.STATUS status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
