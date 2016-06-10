package com.geocento.webapps.eobroker.shared.imageapi;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 03/03/2016.
 */
public class ProductOrderDTO {

    Long id;

    boolean archiveProduct;

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
    OrderParameterDTO licenseOption;
    // used when sending the order
    List<OrderParameterDTO> orderingOptions;

    // the latest price calculation
    PriceDTO imagePrice;
    PriceDTO timePrice;
    PriceDTO totalPrice;
    PriceDTO offeredPrice;

    String fetchUrl;

    List<CommentDTO> comments;
    Date creationTime;
    STATUS status;

    public ProductOrderDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isArchiveProduct() {
        return archiveProduct;
    }

    public void setArchiveProduct(boolean isArchiveProduct) {
        this.archiveProduct = isArchiveProduct;
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

    public PriceDTO getImagePrice() {
        return imagePrice;
    }

    public void setImagePrice(PriceDTO imagePrice) {
        this.imagePrice = imagePrice;
    }

    public PriceDTO getTimePrice() {
        return timePrice;
    }

    public void setTimePrice(PriceDTO timePrice) {
        this.timePrice = timePrice;
    }

    public PriceDTO getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(PriceDTO totalPrice) {
        this.totalPrice = totalPrice;
    }

    public PriceDTO getOfferedPrice() {
        return offeredPrice;
    }

    public void setOfferedPrice(PriceDTO offeredPrice) {
        this.offeredPrice = offeredPrice;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }
}
