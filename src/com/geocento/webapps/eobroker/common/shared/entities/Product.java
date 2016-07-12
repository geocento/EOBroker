package com.geocento.webapps.eobroker.common.shared.entities;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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

    @Column(length = 1000)
    String shortDescription;

    @Column(length = 10000)
    String description;

    @Enumerated(EnumType.STRING)
    Sector sector;

    @Enumerated(EnumType.STRING)
    Thematic thematic;

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

    @OneToMany(mappedBy = "product")
    Set<ProductService> productServices;

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

    public Set<ProductService> getProductServices() {
        return productServices;
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
