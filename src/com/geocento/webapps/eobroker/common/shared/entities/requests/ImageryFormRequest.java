package com.geocento.webapps.eobroker.common.shared.entities.requests;

import com.geocento.webapps.eobroker.common.server.Utils.GeometryConverter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 06/07/2016.
 */
@Entity
public class ImageryFormRequest extends Request {

    @Convert(converter = GeometryConverter.class)
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

    public ImageryFormRequest() {
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
