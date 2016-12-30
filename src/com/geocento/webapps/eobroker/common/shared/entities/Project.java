package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity
public class Project {

    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    Company company;

    @OneToMany
    List<ProductProject> products;

    @Column(length = 1000)
    String name;

    @Column(length = 10000)
    String description;

    @Column(length = 1000)
    String imageUrl;

    @Column(length = 1000)
    String email;

    @Column(length = 1000)
    String website;

    @Column(length = 100000)
    String fullDescription;

    @OneToMany(fetch = FetchType.LAZY)
    List<Company> consortium;

    public Project() {
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

    public List<ProductProject> getProducts() {
        return products;
    }

    public void setProducts(List<ProductProject> products) {
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

    public List<Company> getConsortium() {
        return consortium;
    }

    public void setConsortium(List<Company> consortium) {
        this.consortium = consortium;
    }
}
