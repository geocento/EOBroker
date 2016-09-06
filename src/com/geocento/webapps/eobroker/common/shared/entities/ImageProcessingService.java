package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;

/**
 * Created by thomas on 06/06/2016.
 *
 * service providing automatic image processing in form of an image product
 *
 */
@Entity(name = "imageryservice")
public class ImageProcessingService {

    @Id
    Long id;

    @Column(length = 1000, unique = true)
    String name;

    @Column(length = 1000)
    String iconURL;

    @Column(length = 1000)
    String description;

    @ManyToOne
    Company company;

    @ManyToOne
    ImageProcessingProduct product;

    public ImageProcessingService() {
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setProduct(ImageProcessingProduct product) {
        this.product = product;
    }

    public ImageProcessingProduct getProduct() {
        return product;
    }
}
