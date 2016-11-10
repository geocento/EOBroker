package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;

/**
 * Created by thomas on 07/11/2016.
 */
@Entity
public class ProductSoftware {

    @Id
    @GeneratedValue
    Long id;

    @Column(length = 1000)
    String pitch;

    @ManyToOne
    Software software;

    @ManyToOne
    Product product;

    public ProductSoftware() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
