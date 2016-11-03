package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity
public class ProductDataset {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    Company company;

    @ManyToOne
    Product product;

    @Column(length = 1000)
    String name;

    @Column(length = 10000)
    String description;

    @Column(length = 1000)
    String imageUrl;

    @Column(length = 100000)
    String fullDescription;

    @Embedded
    Extent extent;

    @Column(length = 1000)
    String email;

    @Column(length = 1000)
    String website;

    @Column(length = 1000)
    String apiUrl;

    @Column(length = 1000)
    String sampleWmsUrl;

    public ProductDataset() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public void setImageUrl(String image) {
        this.imageUrl = image;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
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

    public Extent getExtent() {
        return extent;
    }

    public void setExtent(Extent extent) {
        this.extent = extent;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getSampleWmsUrl() {
        return sampleWmsUrl;
    }

    public void setSampleWmsUrl(String sampleWmsUrl) {
        this.sampleWmsUrl = sampleWmsUrl;
    }
}