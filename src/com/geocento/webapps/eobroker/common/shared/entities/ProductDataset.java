package com.geocento.webapps.eobroker.common.shared.entities;

import com.geocento.webapps.eobroker.common.server.Utils.GeometryConverter;
import com.geocento.webapps.eobroker.common.shared.datasets.DatasetStandard;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity
public class ProductDataset {

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
    String name;

    @Column(length = 10000)
    String description;

    @Column(length = 1000)
    String imageUrl;

    @Column(length = 100000)
    String fullDescription;

    @Convert(converter = GeometryConverter.class)
    String extent;

    @Enumerated(EnumType.STRING)
    ServiceType serviceType;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<DatasetAccess> datasetAccesses;

    @JoinTable(name = "productdataset_samples")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<DatasetAccess> samples;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "productdataset_geoinformation")
    List<FeatureDescription> geoinformation;

    @Column(length = 1000)
    String geoinformationComment;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "productdataset_performances")
    List<PerformanceValue> performances;

    @Column(length = 1000)
    String performancesComment;

    @Embedded
    TemporalCoverage temporalCoverage;

    @Column(length = 1000)
    String temporalCoverageComment;

    @Enumerated(EnumType.STRING)
    DatasetStandard datasetStandard;

    @Column(length = 1000)
    String datasetURL;

    @ManyToMany(fetch = FetchType.LAZY)
    List<Standard> applicableStandards;

    @Column(length = 1000)
    String email;

    @Column(length = 1000)
    String website;

    public ProductDataset() {
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

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public List<DatasetAccess> getDatasetAccesses() {
        return datasetAccesses;
    }

    public void setDatasetAccesses(List<DatasetAccess> datasetAccesses) {
        this.datasetAccesses = datasetAccesses;
    }

    public List<DatasetAccess> getSamples() {
        return samples;
    }

    public void setSamples(List<DatasetAccess> samples) {
        this.samples = samples;
    }

    public void setGeoinformation(List<FeatureDescription> geoinformation) {
        this.geoinformation = geoinformation;
    }

    public List<FeatureDescription> getGeoinformation() {
        return geoinformation;
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

    public TemporalCoverage getTemporalCoverage() {
        return temporalCoverage;
    }

    public void setTemporalCoverage(TemporalCoverage temporalCoverage) {
        this.temporalCoverage = temporalCoverage;
    }

    public String getTemporalCoverageComment() {
        return temporalCoverageComment;
    }

    public void setTemporalCoverageComment(String temporalCoverageComment) {
        this.temporalCoverageComment = temporalCoverageComment;
    }

    public DatasetStandard getDatasetStandard() {
        return datasetStandard;
    }

    public void setDatasetStandard(DatasetStandard datasetStandard) {
        this.datasetStandard = datasetStandard;
    }

    public String getDatasetURL() {
        return datasetURL;
    }

    public void setDatasetURL(String datasetURL) {
        this.datasetURL = datasetURL;
    }
}
