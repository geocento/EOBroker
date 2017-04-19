package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by thomas on 13/03/2017.
 */
@Entity
public class Testimonial {

    @GeneratedValue
    @Id
    Long id;

    @ManyToOne
    User fromUser;

    // testimonial can only be on company offerings or the company itself
    @ManyToOne
    Company company;
    @ManyToOne
    ProductService productService;
    @ManyToOne
    ProductDataset productDataset;
    @ManyToOne
    Software software;
    @ManyToOne
    Project project;

    @Column(length = 1000)
    String testimonial;

    @Temporal(TemporalType.TIMESTAMP)
    Date creationDate;

    public Testimonial() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public ProductDataset getProductDataset() {
        return productDataset;
    }

    public void setProductDataset(ProductDataset productDataset) {
        this.productDataset = productDataset;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getTestimonial() {
        return testimonial;
    }

    public void setTestimonial(String testimonial) {
        this.testimonial = testimonial;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
