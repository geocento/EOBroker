package com.geocento.webapps.eobroker.common.shared.imageapi;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * an image scene as returned by a search, can be from a catalogue or to be tasked
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Product {

    /**
     * The type of product
     */
    public static enum TYPE {
        ARCHIVE, PLANNEDACQ, TASKING
    }

    /**
     * The direction of the orbit
     */
    public static enum ORBITDIRECTION {
        ASCENDING, DESCENDING
    }

    // the geocento id for this product
    String productId;
    String providerName;
    TYPE type;

    // sensor parameters
    String satelliteName;
    String instrumentName;
    String modeName;
    String sensorType;
    String sensorBand;
    double sensorResolution;
    String sensorInformationUrl;

    String coordinatesWKT;
    String center;

    Date start;
    Date stop;

    Integer orbit;
    ORBITDIRECTION orbitDirection;
    String ascendingNodeDate;
    String startTimeFromAscendingNode;
    String completionTimeFromAscendingNodeDate;

    // statistics
    double area;
    double aoiCoveragePercent;
	double usefulAreaPercent;
    
    // frame properties
    int frameNumbers = 0;
	
    // optical parameters
	Double oza;
    Double ona;
    Double cloudCoveragePercent;
    Double cloudCoverageStatisticsPercent;
    Double sza;
    String stereoStackName;

    // sar parameters
    String polarisation;

    // quicklooks
	String ql;
    String thumbnail;

    // json string according to sensor options
    String vendorAttributes;

    /*
     * this is the fetch Url if the image is directly available
     */
    String fetchUrl;

    // policy and prices for the product selection
    Long policyId;
    Price selectionPrice;
    Price convertedSelectionPrice;
    String selectionPriceExplanation;

    public Product() {
	}

    /**
     *
     * the geocento id for this scene, to be used internally
     *
     * @return
     */
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     *
     * the name of the image supplier for this scene
     *
     * @return
     */
    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    /**
     *
     * whether the product is an archive scene, a planned acquisition or a potential tasking
     *
     * @return
     */
    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    /**
     *
     * the name of the satellite which acquired the scene
     *
     * @return
     */
    public String getSatelliteName() {
        return satelliteName;
    }

    public void setSatelliteName(String satelliteName) {
        this.satelliteName = satelliteName;
    }

    /**
     *
     * the name of the instrument which acquired the scene
     *
     * @return
     */
    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    /**
     *
     * the name of the mode which acquired the scene
     *
     * @return
     */
    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    /**
     *
     * the type of imagery, eg Optical or SAR
     *
     * @return
     */
    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    /**
     *
     * the sensor band(s)
     *
     * @return
     */
    public String getSensorBand() {
        return sensorBand;
    }

    public void setSensorBand(String sensorBand) {
        this.sensorBand = sensorBand;
    }

    /**
     *
     * the sensor resolution in meters
     *
     * @return
     */
    public double getSensorResolution() {
        return sensorResolution;
    }

    public void setSensorResolution(double sensorResolution) {
        this.sensorResolution = sensorResolution;
    }

    public void setSensorInformationUrl(String sensorInformationUrl) {
        this.sensorInformationUrl = sensorInformationUrl;
    }

    /**
     *
     * a link to the sensor information page
     *
     * @return
     */
    public String getSensorInformationUrl() {
        return sensorInformationUrl;
    }

    /**
     *
     * the product footprint coordinates as a WKT string
     *
     * @return
     */
    public String getCoordinatesWKT() {
        return coordinatesWKT;
    }

    public void setCoordinatesWKT(String coordinatesWKT) {
        this.coordinatesWKT = coordinatesWKT;
    }

    /**
     *
     * the product center coordinate as a WKT string (POINT)
     *
     * @return
     */
    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    /**
     *
     * the acquisition start date as a long timestamp in ms
     *
     * @return
     */
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    /**
     *
     * the acquisition stop date as a long timestamp in ms
     *
     * @return
     */
    public Date getStop() {
        return stop;
    }

    public void setStop(Date stop) {
        this.stop = stop;
    }

    /**
     *
     * the orbit number when available
     *
     * @return
     */
    public Integer getOrbit() {
        return orbit;
    }

    public void setOrbit(Integer orbit) {
        this.orbit = orbit;
    }

    /**
     *
     * the orbit direction when available
     *
     * @return
     */
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

    /**
     *
     * the total area of the scene in square meters
     *
     * @return
     */
    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    /**
     *
     * the percent of the input AoI being covered by this scene
     *
     * @return
     */
    public double getAoiCoveragePercent() {
        return aoiCoveragePercent;
    }

    public void setAoiCoveragePercent(double aoiCoveragePercent) {
        this.aoiCoveragePercent = aoiCoveragePercent;
    }

    /**
     *
     * the percent of the scene that overlaps with the input AoI
     *
     * @return
     */
    public double getUsefulAreaPercent() {
        return usefulAreaPercent;
    }

    public void setUsefulAreaPercent(double usefulAreaPercent) {
        this.usefulAreaPercent = usefulAreaPercent;
    }

    /**
     *
     * the number of frames for this product (if this product uses frames)
     *
     * @return
     */
    public int getFrameNumbers() {
        return frameNumbers;
    }

    public void setFrameNumbers(int frameNumbers) {
        this.frameNumbers = frameNumbers;
    }

    /**
     *
     * the average Observation Azimuth Angle of this scene in degrees, OZA is equivalent to incidence angle in SAR and 90 - elevation angle in optical images
     *
     * @return
     */
    public Double getOza() {
        return oza;
    }

    public void setOza(Double oza) {
        this.oza = oza;
    }

    /**
     *
     * the average Off Nadir Angle of this scene in degrees, ONA is equivalent to Look Angle
     *
     * @return
     */
    public Double getOna() {
        return ona;
    }

    public void setOna(Double ona) {
        this.ona = ona;
    }

    /**
     *
     * (optical only) the percent of the scene covered by cloud
     * - for ARCHIVE products this is for the total scene
     * - for TASKING products this is ESTIMATED based on weather predictions (only for one week ahead) and for the AoI part of scene
     * - for PLANNEDACQ products this is not provided
     *
     * @return
     */
    public Double getCloudCoveragePercent() {
        return cloudCoveragePercent;
    }

    public void setCloudCoveragePercent(Double cloudCoveragePercent) {
        this.cloudCoveragePercent = cloudCoveragePercent;
    }

    /**
     *
     * (optical only) as for cloud coverage percent BUT only for TASKING type of products, when weather forecast is not available, statistics are used
     *
     * @return
     */
    public Double getCloudCoverageStatisticsPercent() {
        return cloudCoverageStatisticsPercent;
    }

    public void setCloudCoverageStatisticsPercent(Double cloudCoverageStatisticsPercent) {
        this.cloudCoverageStatisticsPercent = cloudCoverageStatisticsPercent;
    }

    /**
     *
     * (optical only) the average Sun Zenith Angle for this scene in degrees
     *
     * @return
     */
    public Double getSza() {
        return sza;
    }

    public void setSza(Double sza) {
        this.sza = sza;
    }

    /**
     *
     * (optical only) a unique identifier for a stereo pair or triplet, null if the scene is not part of a stereo pair or triplet
     *
     * @return
     */
    public String getStereoStackName() {
        return stereoStackName;
    }

    public void setStereoStackName(String stereoStackName) {
        this.stereoStackName = stereoStackName;
    }

    /**
     *
     * (ARCHIVE SAR only) return the polarisation mode for the acquired scene
     *
     * @return
     */
    public String getPolarisation() {
        return polarisation;
    }

    public void setPolarisation(String polarisation) {
        this.polarisation = polarisation;
    }

    /**
     *
     * (only for ARCHIVE) URL of the thumbnail image of this scene
     *
     * @return
     */
    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     *
     * (only for ARCHIVE) link to the WMS service for overlaying the scene quicklook on a map
     *
     * @return
     */
    public String getQl() {
        return ql;
    }

    public void setQl(String ql) {
        this.ql = ql;
    }

    /**
     *
     * (only for ARCHIVE) URL to download the product when available, eg free imagery
     *
     * @return
     */
    public String getFetchUrl() {
        return fetchUrl;
    }

    public void setFetchUrl(String fetchUrl) {
        this.fetchUrl = fetchUrl;
    }

    /**
     *
     * all additional attributes provided by the supplier but not covered in the default set of attributes
     * format is a JSON object
     *
     * @return
     */
    public String getVendorAttributes() {
        return vendorAttributes;
    }

    public void setVendorAttributes(String vendorAttributes) {
        this.vendorAttributes = vendorAttributes;
    }

    /**
     *
     * the ordering policy id for this scene
     *
     * @return
     */
    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    /**
     *
     * price estimate in supplier's currency for this scene and the provided AoI
     *
     * @return
     */
    public Price getSelectionPrice() {
        return selectionPrice;
    }

    public void setSelectionPrice(Price selectionPrice) {
        this.selectionPrice = selectionPrice;
    }

    /**
     *
     * price estimate in user specified currency for this scene and the provided AoI
     *
     * @return
     */
    public Price getConvertedSelectionPrice() {
        return convertedSelectionPrice;
    }

    public void setConvertedSelectionPrice(Price convertedSelectionPrice) {
        this.convertedSelectionPrice = convertedSelectionPrice;
    }

    /**
     *
     * a textual explanation for the price estimate
     *
     * @return
     */
    public String getSelectionPriceExplanation() {
        return selectionPriceExplanation;
    }

    public void setSelectionPriceExplanation(String selectionPriceExplanation) {
        this.selectionPriceExplanation = selectionPriceExplanation;
    }

}
