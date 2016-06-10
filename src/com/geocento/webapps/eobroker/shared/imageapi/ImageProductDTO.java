package com.geocento.webapps.eobroker.shared.imageapi;

import com.geocento.webapps.eobroker.shared.LatLng;

import java.util.Date;

// not to be stored, only for passing results and displaying them
public class ImageProductDTO {

    public static enum ORBITDIRECTION {
        ASCENDING, DESCENDING
    }

    // the geocento id for this product
    String productId;
    String providerId;
    boolean archiveProduct;
    boolean plannedAcq;

    // sensor parameters
    Long modeId;
    String satelliteName;
    String sensorName;
    String modeName;
    String sensorType;
    String sensorBand;
    double sensorResolution;
    String sensorInformationUrl;

    String coordinatesWKT;
    LatLng center;

    Date start;
    Date stop;

    Integer orbit;
    ORBITDIRECTION orbitDirection;
    String ascendingNodeDate;
    String startTimeFromAscendingNode;
    String completionTimeFromAscendingNodeDate;

    // statistics
    double areaSquareMeters;
    double aoiCoveragePercent;
	double usefulAreaPercent;
    
    // frame properties
    int frameNumbers = 0;
	
    // optical parameters
	Double oza;
    Double ona;
    Double cloudCoveragePercent;
    Double cloudCoverageStatisticsPercent;
    Double sunZenithAngle;
    String stereoStackName;

    // sar parameters
    String polarisation;

    // quicklooks
	String ql;
    String originalQl;
    String thumbnail;

    /*
     * this is the fetch Url if the image is directly available
     */
    String fetchUrl;

    // prices for the product selection
    PriceDTO selectionPrice;
    PriceDTO convertedSelectionPrice;

    // prices for coverage of AoI
    PriceDTO coveragePrice;
    PriceDTO convertedCoveragePrice;

    String selectionPriceExplanation;
    String coveragePriceExplanation;

    // json string according to sensor options
    String vendorAttributes;

    public ImageProductDTO() {
	}
    
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public boolean isArchiveProduct() {
        return archiveProduct;
    }

    public void setArchiveProduct(boolean isArchiveProduct) {
        this.archiveProduct = isArchiveProduct;
    }

    public boolean isPlannedAcq() {
        return plannedAcq;
    }

    public void setPlannedAcq(boolean isPlannedAcq) {
        this.plannedAcq = isPlannedAcq;
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

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getSensorBand() {
        return sensorBand;
    }

    public void setSensorBand(String sensorBand) {
        this.sensorBand = sensorBand;
    }

    public double getSensorResolution() {
        return sensorResolution;
    }

    public void setSensorResolution(double sensorResolution) {
        this.sensorResolution = sensorResolution;
    }

    public void setSensorInformationUrl(String sensorInformationUrl) {
        this.sensorInformationUrl = sensorInformationUrl;
    }

    public String getSensorInformationUrl() {
        return sensorInformationUrl;
    }

    public String getCoordinatesWKT() {
        return coordinatesWKT;
    }

    public void setCoordinatesWKT(String coordinatesWKT) {
        this.coordinatesWKT = coordinatesWKT;
    }

/*
    public EOLatLng getCenter() {
        return center;
    }

    public void setCenter(EOLatLng center) {
        this.center = center;
    }

*/
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

    public Integer getOrbit() {
        return orbit;
    }

    public void setOrbit(Integer orbit) {
        this.orbit = orbit;
    }

    public ORBITDIRECTION getOrbitDirection() {
        return orbitDirection;
    }

    public void setOrbitDirection(ORBITDIRECTION orbitDirection) {
        this.orbitDirection = orbitDirection;
    }

    public String getAscendingNodeDate() {
        return ascendingNodeDate;
    }

    public void setAscendingNodeDate(String ascendingNodeDate) {
        this.ascendingNodeDate = ascendingNodeDate;
    }

    public String getStartTimeFromAscendingNode() {
        return startTimeFromAscendingNode;
    }

    public void setStartTimeFromAscendingNode(String startTimeFromAscendingNode) {
        this.startTimeFromAscendingNode = startTimeFromAscendingNode;
    }

    public String getCompletionTimeFromAscendingNodeDate() {
        return completionTimeFromAscendingNodeDate;
    }

    public void setCompletionTimeFromAscendingNodeDate(String completionTimeFromAscendingNodeDate) {
        this.completionTimeFromAscendingNodeDate = completionTimeFromAscendingNodeDate;
    }

    public double getAreaSquareMeters() {
        return areaSquareMeters;
    }

    public void setAreaSquareMeters(double areaSquareMeters) {
        this.areaSquareMeters = areaSquareMeters;
    }

    public double getAoiCoveragePercent() {
        return aoiCoveragePercent;
    }

    public void setAoiCoveragePercent(double aoiCoveragePercent) {
        this.aoiCoveragePercent = aoiCoveragePercent;
    }

    public double getUsefulAreaPercent() {
        return usefulAreaPercent;
    }

    public void setUsefulAreaPercent(double usefulAreaPercent) {
        this.usefulAreaPercent = usefulAreaPercent;
    }

    public int getFrameNumbers() {
        return frameNumbers;
    }

    public void setFrameNumbers(int frameNumbers) {
        this.frameNumbers = frameNumbers;
    }

    public Double getOza() {
        return oza;
    }

    public void setOza(Double oza) {
        this.oza = oza;
    }

    public Double getOna() {
        return ona;
    }

    public void setOna(Double ona) {
        this.ona = ona;
    }

    public Double getCloudCoveragePercent() {
        return cloudCoveragePercent;
    }

    public void setCloudCoveragePercent(Double cloudCoveragePercent) {
        this.cloudCoveragePercent = cloudCoveragePercent;
    }

    public Double getCloudCoverageStatisticsPercent() {
        return cloudCoverageStatisticsPercent;
    }

    public void setCloudCoverageStatisticsPercent(Double cloudCoverageStatisticsPercent) {
        this.cloudCoverageStatisticsPercent = cloudCoverageStatisticsPercent;
    }

    public Double getSunZenithAngle() {
        return sunZenithAngle;
    }

    public void setSunZenithAngle(Double sunZenithAngle) {
        this.sunZenithAngle = sunZenithAngle;
    }

    public String getStereoStackName() {
        return stereoStackName;
    }

    public void setStereoStackName(String stereoStackName) {
        this.stereoStackName = stereoStackName;
    }

    public String getPolarisation() {
        return polarisation;
    }

    public void setPolarisation(String polarisation) {
        this.polarisation = polarisation;
    }

    public String getQl() {
        return ql;
    }

    public void setQl(String ql) {
        this.ql = ql;
    }

    public String getOriginalQl() {
        return originalQl;
    }

    public void setOriginalQl(String originalQl) {
        this.originalQl = originalQl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFetchUrl() {
        return fetchUrl;
    }

    public void setFetchUrl(String fetchUrl) {
        this.fetchUrl = fetchUrl;
    }

    public PriceDTO getSelectionPrice() {
        return selectionPrice;
    }

    public void setSelectionPrice(PriceDTO selectionPrice) {
        this.selectionPrice = selectionPrice;
    }

    public PriceDTO getConvertedSelectionPrice() {
        return convertedSelectionPrice;
    }

    public void setConvertedSelectionPrice(PriceDTO convertedSelectionPrice) {
        this.convertedSelectionPrice = convertedSelectionPrice;
    }

    public PriceDTO getCoveragePrice() {
        return coveragePrice;
    }

    public void setCoveragePrice(PriceDTO coveragePrice) {
        this.coveragePrice = coveragePrice;
    }

    public PriceDTO getConvertedCoveragePrice() {
        return convertedCoveragePrice;
    }

    public void setConvertedCoveragePrice(PriceDTO convertedCoveragePrice) {
        this.convertedCoveragePrice = convertedCoveragePrice;
    }

    public String getSelectionPriceExplanation() {
        return selectionPriceExplanation;
    }

    public void setSelectionPriceExplanation(String selectionPriceExplanation) {
        this.selectionPriceExplanation = selectionPriceExplanation;
    }

    public String getCoveragePriceExplanation() {
        return coveragePriceExplanation;
    }

    public void setCoveragePriceExplanation(String coveragePriceExplanation) {
        this.coveragePriceExplanation = coveragePriceExplanation;
    }

    public String getVendorAttributes() {
        return vendorAttributes;
    }

    public void setVendorAttributes(String vendorAttributes) {
        this.vendorAttributes = vendorAttributes;
    }
}
