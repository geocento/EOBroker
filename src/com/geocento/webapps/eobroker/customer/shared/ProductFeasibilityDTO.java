package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.FormElement;

import java.util.List;

/**
 * Created by thomas on 23/06/2016.
 */
public class ProductFeasibilityDTO {

    Long id;
    String name;
    String imageUrl;
    String shortDescription;
    List<FormElement> apiFormElements;
    private List<ProductServiceFeasibilityDTO> productServices;

    public ProductFeasibilityDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public List<FormElement> getApiFormElements() {
        return apiFormElements;
    }

    public void setApiFormElements(List<FormElement> apiFormElements) {
        this.apiFormElements = apiFormElements;
    }

    public List<ProductServiceFeasibilityDTO> getProductServices() {
        return productServices;
    }

    public void setProductServices(List<ProductServiceFeasibilityDTO> productServices) {
        this.productServices = productServices;
    }
}
