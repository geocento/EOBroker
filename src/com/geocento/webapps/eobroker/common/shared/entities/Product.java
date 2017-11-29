package com.geocento.webapps.eobroker.common.shared.entities;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity
public class Product {

    @Id
    @GeneratedValue
    Long id;

    @Column(length = 1000, unique = true)
    String name;

    @Column(length = 1000)
    String imageUrl;

    @OneToMany
    List<ProductCategory> categories;

    @Column(length = 1000)
    String shortDescription;

    @Column(length = 10000)
    String description;

    @Enumerated(EnumType.STRING)
    Sector sector;

    @Enumerated(EnumType.STRING)
    Thematic thematic;

    @OrderColumn(name = "geoinformation_order")
    @OneToMany(cascade = CascadeType.ALL)
    List<FeatureDescription> geoinformation;

    @OrderColumn(name = "performance_order")
    @OneToMany(cascade = CascadeType.ALL)
    List<PerformanceDescription> performances;

    @ManyToMany(fetch = FetchType.LAZY)
    List<Standard> applicableStandards;

    @OrderColumn(name = "form_order")
    @JoinTable(name = "product_formelement")
    @OneToMany(cascade = CascadeType.ALL)
    List<FormElement> formFields;

    @OrderColumn(name = "apiform_order")
    @JoinTable(name = "product_apielement")
    @OneToMany(cascade = CascadeType.ALL)
    List<FormElement> apiFormFields;

    @Column(length = 10000)
    String recommendationRule;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    List<ProductService> productServices;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    List<ProductDataset> productDatasets;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product")
    List<SuccessStory> successStories;

    @ManyToMany(fetch = FetchType.LAZY)
    List<Challenge> challenges;

    Long followers;

    public Product() {
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

    public List<ProductCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<ProductCategory> categories) {
        this.categories = categories;
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public List<FeatureDescription> getGeoinformation() {
        return geoinformation;
    }

    public void setGeoinformation(List<FeatureDescription> geoinformation) {
        this.geoinformation = geoinformation;
    }

    public List<PerformanceDescription> getPerformances() {
        return performances;
    }

    public void setPerformances(List<PerformanceDescription> performances) {
        this.performances = performances;
    }

    public List<Standard> getApplicableStandards() {
        return applicableStandards;
    }

    public void setApplicableStandards(List<Standard> applicableStandards) {
        this.applicableStandards = applicableStandards;
    }

    public List<FormElement> getFormFields() {
        return formFields;
    }

    public void setFormFields(List<FormElement> formFields) {
        this.formFields = formFields;
    }

    public List<FormElement> getApiFormFields() {
        return apiFormFields;
    }

    public void setApiFormFields(List<FormElement> apiFormFields) {
        this.apiFormFields = apiFormFields;
    }

    public String getRecommendationRule() {
        return recommendationRule;
    }

    public void setRecommendationRule(String recommendationRule) {
        this.recommendationRule = recommendationRule;
    }

    public List<ProductService> getProductServices() {
        return productServices;
    }

    public void setProductServices(List<ProductService> productServices) {
        this.productServices = productServices;
    }

    public List<ProductDataset> getProductDatasets() {
        return productDatasets;
    }

    public void setProductDatasets(List<ProductDataset> productDatasets) {
        this.productDatasets = productDatasets;
    }

    public void setFollowers(Long followers) {
        this.followers = followers;
    }

    public Long getFollowers() {
        return followers;
    }

    public List<SuccessStory> getSuccessStories() {
        return successStories;
    }

    public void setSuccessStories(List<SuccessStory> successStories) {
        this.successStories = successStories;
    }

    @PreRemove
    private void removeProduct() {
        if(productServices == null) {
            return;
        }
        for(ProductService productService : productServices) {
            productService.setProduct(null);
        }
    }

}
