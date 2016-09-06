package com.geocento.webapps.eobroker.common.shared.entities.orders;

import com.geocento.webapps.eobroker.common.shared.entities.User;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 06/07/2016.
 */
@Entity
public class ImageryFormRequest {

    @Id
    String id;

    @ManyToOne
    User customer;

    @Column(length = 1000)
    String aoiWKT;

    @Column(length = 1000)
    String imageType;

    @Temporal(TemporalType.TIMESTAMP)
    Date start;
    @Temporal(TemporalType.TIMESTAMP)
    Date stop;

    @Column(length = 1000)
    String additionalInformation;

    @OneToMany(mappedBy = "imageryFormRequest", cascade = CascadeType.ALL)
    List<ImageServiceFormRequest> imageServiceRequests;

    public ImageryFormRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getStop() {
        return stop;
    }

    public void setStop(Date stop) {
        this.stop = stop;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public List<ImageServiceFormRequest> getImageServiceRequests() {
        return imageServiceRequests;
    }

    public void setImageServiceRequests(List<ImageServiceFormRequest> imageServiceRequests) {
        this.imageServiceRequests = imageServiceRequests;
    }
}
