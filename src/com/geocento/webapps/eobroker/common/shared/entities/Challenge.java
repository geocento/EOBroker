package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity
public class Challenge {

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

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "challenges")
    List<Product> products;

    public Challenge() {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
