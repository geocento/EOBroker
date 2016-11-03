package com.geocento.webapps.eobroker.common.shared.entities.software;

import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.Product;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 07/10/2016.
 */
@Entity
public class Software {

    @Id
    @GeneratedValue
    Long id;

    @Column(unique = true)
    String name;

    @Column(length = 1000)
    String iconUrl;

    @Column(length = 10000)
    String description;

    @Column(length = 1000)
    String website;

    @Column(length = 1000)
    String processingWebsite;

    @Column(length = 10000)
    String licensing;

    @ManyToMany
    List<Product> generatedProducts;

    @ManyToOne
    Company company;

    public Software() {
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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getProcessingWebsite() {
        return processingWebsite;
    }

    public void setProcessingWebsite(String processingWebsite) {
        this.processingWebsite = processingWebsite;
    }

    public String getLicensing() {
        return licensing;
    }

    public void setLicensing(String licensing) {
        this.licensing = licensing;
    }

    public List<Product> getGeneratedProducts() {
        return generatedProducts;
    }

    public void setGeneratedProducts(List<Product> generatedProducts) {
        this.generatedProducts = generatedProducts;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
