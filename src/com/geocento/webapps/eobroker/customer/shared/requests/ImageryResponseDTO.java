package com.geocento.webapps.eobroker.customer.shared.requests;

import com.geocento.webapps.eobroker.common.shared.entities.requests.Request;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 11/07/2016.
 */
public class ImageryResponseDTO {

    String id;
    String aoiWKT;
    String imageType;
    Date start;
    Date stop;
    String additionalInformation;
    String application;
    Date creationDate;
    List<ImagerySupplierResponseDTO> supplierResponses;
    Request.STATUS status;

    public ImageryResponseDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setApplication(String application) {
        this.application = application;
    }

    public String getApplication() {
        return application;
    }

    public List<ImagerySupplierResponseDTO> getSupplierResponses() {
        return supplierResponses;
    }

    public void setSupplierResponses(List<ImagerySupplierResponseDTO> supplierResponses) {
        this.supplierResponses = supplierResponses;
    }

    public void setStatus(Request.STATUS status) {
        this.status = status;
    }

    public Request.STATUS getStatus() {
        return status;
    }
}
