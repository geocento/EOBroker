package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity
public class Software {

    @Id
    @GeneratedValue
    Long id;

    @Enumerated(EnumType.STRING)
    PUBLICATION_STATE publicationState;

    @ManyToOne
    Company company;

    @OneToMany(mappedBy = "software", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<ProductSoftware> products;

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

    @Enumerated(EnumType.STRING)
    SoftwareType softwareType;

    @Column(length = 100000)
    String termsAndConditions;

    public Software() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PUBLICATION_STATE getPublicationState() {
        return publicationState;
    }

    public void setPublicationState(PUBLICATION_STATE publicationState) {
        this.publicationState = publicationState;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<ProductSoftware> getProducts() {
        return products;
    }

    public void setProducts(List<ProductSoftware> products) {
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

    public SoftwareType getSoftwareType() {
        return softwareType;
    }

    public void setSoftwareType(SoftwareType softwareType) {
        this.softwareType = softwareType;
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

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }
}
