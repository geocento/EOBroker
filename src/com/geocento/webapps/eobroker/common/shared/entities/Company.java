package com.geocento.webapps.eobroker.common.shared.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity
public class Company {

    @Id
    @GeneratedValue
    Long id;

    @Column(length = 1000, unique = true)
    String name;

    @Column(length = 1000)
    String iconURL;

    @Column(length = 1000)
    String description;

    @Column(length = 10000)
    private String fullDescription;

    @Column(length = 1000)
    private String contactEmail;

    @Column(length = 1000)
    private String website;

    @Temporal(TemporalType.TIMESTAMP)
    Date startedIn;

    @Column(length = 1000)
    String address;

    @Column(length = 3)
    String countryCode;

    @Enumerated(EnumType.STRING)
    COMPANY_SIZE companySize;

    @ElementCollection
    List<String> awards;

    // TODO - check if we need testimonials if we have success stories
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    List<Testimonial> testimonials;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplier")
    List<SuccessStory> successStories;

    @OneToOne
    SupplierSettings settings;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    List<ProductService> services;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    List<ProductDataset> datasets;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    List<Software> software;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    List<Project> projects;

    public Company() {
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

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
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

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Date getStartedIn() {
        return startedIn;
    }

    public void setStartedIn(Date startedIn) {
        this.startedIn = startedIn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public COMPANY_SIZE getCompanySize() {
        return companySize;
    }

    public void setCompanySize(COMPANY_SIZE companySize) {
        this.companySize = companySize;
    }

    public List<String> getAwards() {
        return awards;
    }

    public void setAwards(List<String> awards) {
        this.awards = awards;
    }

    public List<Testimonial> getTestimonials() {
        return testimonials;
    }

    public void setTestimonials(List<Testimonial> testimonials) {
        this.testimonials = testimonials;
    }

    public List<SuccessStory> getSuccessStories() {
        return successStories;
    }

    public void setSuccessStories(List<SuccessStory> successStories) {
        this.successStories = successStories;
    }

    public SupplierSettings getSettings() {
        return settings;
    }

    public void setSettings(SupplierSettings settings) {
        this.settings = settings;
    }

    public List<ProductService> getServices() {
        return services;
    }

    public void setServices(List<ProductService> services) {
        this.services = services;
    }

    public List<ProductDataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<ProductDataset> datasets) {
        this.datasets = datasets;
    }

    public List<Software> getSoftware() {
        return software;
    }

    public void setSoftware(List<Software> software) {
        this.software = software;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
