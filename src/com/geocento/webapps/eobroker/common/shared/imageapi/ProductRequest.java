package com.geocento.webapps.eobroker.common.shared.imageapi;

import java.util.Date;
import java.util.List;

/**
 *
 * NOT AVAILABLE YET - a product request, before it is actually converted into an order
 */
public class ProductRequest {

    // specify either a catalogue request or a tasking request
    ProductQuery catalogueRequest;
    Tasking tasking;

    String aoiWKT;

    // license option
    OrderParameter licenseOption;
    // used when sending the order
    List<OrderParameter> orderingOptions;

    String comment;
    Date creationTime;

    public ProductRequest() {
    }

    public ProductQuery getCatalogueRequest() {
        return catalogueRequest;
    }

    public void setCatalogueRequest(ProductQuery catalogueRequest) {
        this.catalogueRequest = catalogueRequest;
    }

    public Tasking getTasking() {
        return tasking;
    }

    public void setTasking(Tasking tasking) {
        this.tasking = tasking;
    }

    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    public OrderParameter getLicenseOption() {
        return licenseOption;
    }

    public void setLicenseOption(OrderParameter licenseOption) {
        this.licenseOption = licenseOption;
    }

    public List<OrderParameter> getOrderingOptions() {
        return orderingOptions;
    }

    public void setOrderingOptions(List<OrderParameter> orderingOptions) {
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
