package com.geocento.webapps.eobroker.customer.shared;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 07/07/2016.
 */
public class ImageRequestDTO {

    String aoiWKT;
    String imageType;
    Date start;
    Date stop;
    String additionalInformation;
    List<Long> imageServiceIds;

    public ImageRequestDTO() {
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

    public List<Long> getImageServiceIds() {
        return imageServiceIds;
    }

    public void setImageServiceIds(List<Long> imageServiceIds) {
        this.imageServiceIds = imageServiceIds;
    }
}
