package com.geocento.webapps.eobroker.common.shared.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
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
