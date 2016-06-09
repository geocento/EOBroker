package com.geocento.webapps.eobroker.shared.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity
public class ProductService {

    @Id
    Long id;

    @OneToMany
    Company company;

    @OneToMany
    Product product;

    @Column(length = 1000)
    String name;

    @Column(length = 10000)
    String description;

    public ProductService() {
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
}
