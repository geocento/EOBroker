package com.geocento.webapps.eobroker.supplier.shared.dtos;

import java.util.Date;

/**
 * Created by thomas on 11/07/2016.
 */
public class ImageryServiceRequestDTO extends BaseRequestDTO {

    String aoiWKT;
    String imageType;
    Date start;
    Date stop;
    String additionalInformation;
    String supplierResponse;
    Date creationDate;
    String application;

    public ImageryServiceRequestDTO() {
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

    public String getSupplierResponse() {
        return supplierResponse;
    }

    public void setSupplierResponse(String supplierResponse) {
        this.supplierResponse = supplierResponse;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getApplication() {
        return application;
    }
}
