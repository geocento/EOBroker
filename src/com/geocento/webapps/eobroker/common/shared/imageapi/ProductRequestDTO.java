package com.geocento.webapps.eobroker.common.shared.imageapi;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 03/03/2016.
 */
public class ProductRequestDTO {

    // specify either a catalogue request or a tasking request
    CatalogueRequestDTO catalogueRequest;
    TaskingDTO tasking;

    String aoiWKT;

    // license option
    OrderParameterDTO licenseOption;
    // used when sending the order
    List<OrderParameterDTO> orderingOptions;

    String comment;
    Date creationTime;

    public ProductRequestDTO() {
    }

    public CatalogueRequestDTO getCatalogueRequest() {
        return catalogueRequest;
    }

    public void setCatalogueRequest(CatalogueRequestDTO catalogueRequest) {
        this.catalogueRequest = catalogueRequest;
    }

    public TaskingDTO getTasking() {
        return tasking;
    }

    public void setTasking(TaskingDTO tasking) {
        this.tasking = tasking;
    }

    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    public OrderParameterDTO getLicenseOption() {
        return licenseOption;
    }

    public void setLicenseOption(OrderParameterDTO licenseOption) {
        this.licenseOption = licenseOption;
    }

    public List<OrderParameterDTO> getOrderingOptions() {
        return orderingOptions;
    }

    public void setOrderingOptions(List<OrderParameterDTO> orderingOptions) {
        this.orderingOptions = orderingOptions;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
