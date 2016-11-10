package com.geocento.webapps.eobroker.supplier.shared.dtos;

import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class SoftwareDTO {

    Long id;
    List<ProductSoftwareDTO> products;
    String name;
    String description;
    String imageUrl;
    String email;
    String website;
    String fullDescription;

    public SoftwareDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ProductSoftwareDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductSoftwareDTO> products) {
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }
}
