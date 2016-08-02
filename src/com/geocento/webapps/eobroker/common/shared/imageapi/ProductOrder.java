package com.geocento.webapps.eobroker.common.shared.imageapi;

import java.util.Date;
import java.util.List;

/**
 *
 * NOT AVAILABLE YET - a product order structure used once an order was made
 */
public class ProductOrder {

    Long id;

    boolean isArchiveProduct;

    String productId;

    Long modeId;
    String satelliteName;
    String instrumentName;
    String modeName;

    String coordinatesWKT;
    String selectionGeometryWKT;

    Date start;
    Date stop;

    String aoiWKT;

    // license option
    OrderParameter licenseOption;
    // used when sending the order
    List<OrderParameter> orderingOptions;

    // the latest price calculation
    Price imagePrice;
    Price timePrice;
    Price totalPrice;
    Price offeredPrice;

    String fetchUrl;

    List<Comment> comments;
    Date creationTime;
    Order.STATUS status;

    public ProductOrder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isArchiveProduct() {
        return isArchiveProduct;
    }

    public void setArchiveProduct(boolean isArchiveProduct) {
        this.isArchiveProduct = isArchiveProduct;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getFetchUrl() {
        return fetchUrl;
    }

    public void setFetchUrl(String fetchUrl) {
        this.fetchUrl = fetchUrl;
    }

    public Long getModeId() {
        return modeId;
    }

    public void setModeId(Long modeId) {
        this.modeId = modeId;
    }

    public String getSatelliteName() {
        return satelliteName;
    }

    public void setSatelliteName(String satelliteName) {
        this.satelliteName = satelliteName;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public String getCoordinatesWKT() {
        return coordinatesWKT;
    }

    public void setCoordinatesWKT(String coordinatesWKT) {
        this.coordinatesWKT = coordinatesWKT;
    }

    public String getSelectionGeometryWKT() {
        return selectionGeometryWKT;
    }

    public void setSelectionGeometryWKT(String selectionGeometryWKT) {
        this.selectionGeometryWKT = selectionGeometryWKT;
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

    public Price getImagePrice() {
        return imagePrice;
    }

    public void setImagePrice(Price imagePrice) {
        this.imagePrice = imagePrice;
    }

    public Price getTimePrice() {
        return timePrice;
    }

    public void setTimePrice(Price timePrice) {
        this.timePrice = timePrice;
    }

    public Price getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Price totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Price getOfferedPrice() {
        return offeredPrice;
    }

    public void setOfferedPrice(Price offeredPrice) {
        this.offeredPrice = offeredPrice;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Order.STATUS getStatus() {
        return status;
    }

    public void setStatus(Order.STATUS status) {
        this.status = status;
    }
}
