package com.geocento.webapps.eobroker.common.shared.entities;

import com.geocento.webapps.eobroker.common.server.Utils.GeometryConverter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity
public class ProductService {

    @Id
    @GeneratedValue
    Long id;

    @Enumerated(EnumType.STRING)
    PUBLICATION_STATE publicationState;

    @ManyToOne
    Company company;

    @ManyToOne
    Product product;

    @Column(length = 1000)
    String imageUrl;

    @Column(length = 1000)
    String name;

    @Column(length = 10000)
    String description;

    @Column(length = 100000)
    String fullDescription;

    @Convert(converter = GeometryConverter.class)
    String extent;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "productservice_geoinformation")
    List<FeatureDescription> geoinformation;

    @Column(length = 1000)
    String geoinformationComment;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "productservice_performances")
    List<PerformanceValue> performances;

    @Column(length = 1000)
    String performancesComment;

    @Column(length = 1000)
    String disseminationComment;

    @ManyToMany(fetch = FetchType.LAZY)
    List<Standard> applicableStandards;

    @Column(length = 1000)
    String email;

    @Column(length = 1000)
    String website;

    @Column(length = 1000)
    String apiUrl;

    @JoinTable(name = "productservice_samples")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<DatasetAccess> samples;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    List<AccessType> selectedAccessTypes;

    @Column
    String timeToDelivery;

    public ProductService() {
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

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public List<FeatureDescription> getGeoinformation() {
        return geoinformation;
    }

    public void setGeoinformation(List<FeatureDescription> geoinformation) {
        this.geoinformation = geoinformation;
    }

    public String getGeoinformationComment() {
        return geoinformationComment;
    }

    public void setGeoinformationComment(String geoinformationComment) {
        this.geoinformationComment = geoinformationComment;
    }

    public List<PerformanceValue> getPerformances() {
        return performances;
    }

    public void setPerformances(List<PerformanceValue> performances) {
        this.performances = performances;
    }

    public String getPerformancesComment() {
        return performancesComment;
    }

    public void setPerformancesComment(String performancesComment) {
        this.performancesComment = performancesComment;
    }

    public List<Standard> getApplicableStandards() {
        return applicableStandards;
    }

    public void setApplicableStandards(List<Standard> applicableStandards) {
        this.applicableStandards = applicableStandards;
    }

    public String getTimeToDelivery() {
        return timeToDelivery;
    }

    public void setTimeToDelivery(String timeToDelivery) {
        this.timeToDelivery = timeToDelivery;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public List<DatasetAccess> getSamples() {
        return samples;
    }

    public void setSamples(List<DatasetAccess> samples) {
        this.samples = samples;
    }

    public List<AccessType> getSelectedAccessTypes() {
        return selectedAccessTypes;
    }

    public void setSelectedAccessTypes(List<AccessType> selectedAccessTypes) {
        this.selectedAccessTypes = selectedAccessTypes;
    }

    public String getDisseminationComment() {
        return disseminationComment;
    }

    public void setDisseminationComment(String disseminationComment) {
        this.disseminationComment = disseminationComment;
    }
}
