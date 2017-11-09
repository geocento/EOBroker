package com.geocento.webapps.eobroker.supplier.shared.dtos;

import java.util.Date;

/**
 * Created by thomas on 13/03/2017.
 */
public class EndorsementDTO {

    Long id;
    UserDTO fromUser;
    String testimonial;
    Date creationDate;

    public EndorsementDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserDTO fromUser) {
        this.fromUser = fromUser;
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
