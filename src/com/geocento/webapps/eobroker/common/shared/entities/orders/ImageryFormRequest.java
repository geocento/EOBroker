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

    @Column(length = 100)
    String application;

    @Column(length = 1000)
    String additionalInformation;

    @OneToMany(mappedBy = "imageryFormRequest", cascade = CascadeType.ALL)
    List<ImageryFormSupplierRequest> imageServiceRequests;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<ImageryFormSupplierRequest> getImageServiceRequests() {
        return imageServiceRequests;
    }

    public void setImageServiceRequests(List<ImageryFormSupplierRequest> imageServiceRequests) {
        this.imageServiceRequests = imageServiceRequests;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getApplication() {
        return application;
    }
}
