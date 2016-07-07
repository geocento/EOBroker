package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.Sector;
import com.geocento.webapps.eobroker.common.shared.entities.Thematic;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;

import java.util.List;

/**
 * Created by thomas on 23/06/2016.
 */
public class ProductFormDTO {

    Long id;
    String name;
    String imageUrl;
    String shortDescription;
    String description;
    Sector sector;
    Thematic thematic;
    List<FormElement> formFields;
    private List<ProductServiceDTO> productServices;

    public ProductFormDTO() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public Thematic getThematic() {
        return thematic;
    }

    public void setThematic(Thematic thematic) {
        this.thematic = thematic;
    }

    public List<FormElement> getFormFields() {
        return formFields;
    }

    public void setFormFields(List<FormElement> formFields) {
        this.formFields = formFields;
    }

    public void setProductServices(List<ProductServiceDTO> productServices) {
        this.productServices = productServices;
    }

    public List<ProductServiceDTO> getProductServices() {
        return productServices;
    }
}
