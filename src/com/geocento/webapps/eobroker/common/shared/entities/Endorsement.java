package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by thomas on 13/03/2017.
 */
@Entity
public class Endorsement {

    @GeneratedValue
    @Id
    Long id;

    @ManyToOne
    User fromUser;

    @ManyToOne
    SuccessStory successStory;

    @Column(length = 1000)
    String testimonial;

    @Temporal(TemporalType.TIMESTAMP)
    Date creationDate;

    public Endorsement() {
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

    public SuccessStory getSuccessStory() {
        return successStory;
    }

    public void setSuccessStory(SuccessStory successStory) {
        this.successStory = successStory;
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
