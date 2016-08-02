package com.geocento.webapps.eobroker.common.shared.imageapi;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The search constraints when running a search.
 * These constraints are applied at run time when searching for suitable scenes, in addition to the sensor, AoI and period of interest
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class ProductFilters {

    Double coverage;
    Double cloud;
    Double[] elevation;
    Double[] ona;
    String direction;
    Double[] incidence;
    Double[] sza;
    Integer stereo;
    String polarisation;

    public ProductFilters() {
    }

    /**
     *
     * minimum coverage of the AoI required in percent
     *
     * @return
     */
    public Double getCoverage() {
        return coverage;
    }

    public void setCoverage(Double coverage) {
        this.coverage = coverage;
    }

    /**
     *
     * maximum cloud cover in percent
     *
     * @return
     */
    public Double getCloud() {
        return cloud;
    }

    public void setCloud(Double cloud) {
        this.cloud = cloud;
    }

    /**
     *
     * (optical only) min and max average Elevation Angle
     *
     * @return
     */
    public Double[] getElevation() {
        return elevation;
    }

    public void setElevation(Double[] elevation) {
        this.elevation = elevation;
    }

    /**
     *
     * (optical only) min and max average Off Nadir Angle
     *
     * @return
     */
    public Double[] getOna() {
        return ona;
    }

    public void setOna(Double[] ona) {
        this.ona = ona;
    }

    /**
     *
     * (optical only) min and max average Sun Zenith Angle in degrees
     *
     * @return
     */
    public Double[] getSza() {
        return sza;
    }

    public void setSza(Double[] sza) {
        this.sza = sza;
    }

    /**
     *
     * (optical only) min number of images in the stereo stack, currently limited to 2 or 3 (stereo or triplet)
     *
     * @return
     */
    public Integer getStereo() {
        return stereo;
    }

    public void setStereo(Integer stereo) {
        this.stereo = stereo;
    }

    /**
     *
     * (SAR only) the required orbit direction, values are ASCENDING or DESCENDING
     *
     * @return
     */
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     *
     * (SAR only) min and max average incidence angles in degrees
     *
     * @return
     */
    public Double[] getIncidence() {
        return incidence;
    }

    public void setIncidence(Double[] incidence) {
        this.incidence = incidence;
    }

    /**
     *
     * (SAR ARCHIVE only) polarisation mode filtering
     *
     * @return
     */
    public String getPolarisation() {
        return polarisation;
    }

    public void setPolarisation(String polarisation) {
        this.polarisation = polarisation;
    }

/*
    */
/** Deserializes an Object of class MyClass from its JSON representation *//*

    public static ProductFilters valueOf(String jsonRepresentation) {
        ObjectMapper mapper = new ObjectMapper(); //Jackson's JSON marshaller
        ProductFilters productFilters = null;
        try {
            productFilters = mapper.readValue(jsonRepresentation, ProductFilters.class );
        } catch (IOException e) {
            throw new WebApplicationException();
        }
        return productFilters;
    }
*/

}
