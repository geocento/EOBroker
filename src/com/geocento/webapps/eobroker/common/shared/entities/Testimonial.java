package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by thomas on 13/03/2017.
 */
@Entity
public class Testimonial {

    @Id
    Long id;

    @ManyToOne
    User fromUser;

    @ManyToOne
    Company company;

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
