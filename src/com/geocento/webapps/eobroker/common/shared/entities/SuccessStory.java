package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity
public class SuccessStory {

    @Id
    @GeneratedValue
    Long id;

    @Enumerated(EnumType.STRING)
    PUBLICATION_STATE publicationState;

    @ManyToOne(fetch = FetchType.LAZY)
    Company supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    Company customer;

    @ManyToOne(fetch = FetchType.LAZY)
    Product product;

    @ManyToMany(fetch = FetchType.LAZY)
    List<ProductService> productServices;

    @ManyToMany(fetch = FetchType.LAZY)
    List<ProductDataset> productDatasets;

    @ManyToMany(fetch = FetchType.LAZY)
    List<Software> software;

    @OneToMany(mappedBy = "successStory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Endorsement> endorsements;

    @Column(length = 1000)
    String name;

    @Column(length = 1000)
    String imageUrl;

    @Column(length = 10000)
    String description;

    @Column(length = 100000)
    String fullDescription;

    @OneToMany(fetch = FetchType.LAZY)
    List<CompanyRole> partners;

    @Temporal(TemporalType.TIMESTAMP)
    Date date;

    public SuccessStory() {
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

    public Company getSupplier() {
        return supplier;
    }

    public void setSupplier(Company supplier) {
        this.supplier = supplier;
    }

    public Company getCustomer() {
        return customer;
    }

    public void setCustomer(Company customer) {
        this.customer = customer;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public List<Endorsement> getEndorsements() {
        return endorsements;
    }

    public void setEndorsements(List<Endorsement> endorsements) {
        this.endorsements = endorsements;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<CompanyRole> getPartners() {
        return partners;
    }

    public void setPartners(List<CompanyRole> partners) {
        this.partners = partners;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
