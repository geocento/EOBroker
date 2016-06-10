package com.geocento.webapps.eobroker.shared.imageapi;

/**
 * Created by thomas on 03/03/2016.
 */
public class ProductFilters {

    Double coverage;
    Double usefulArea;
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

    public Double getCoverage() {
        return coverage;
    }

    public void setCoverage(Double coverage) {
        this.coverage = coverage;
    }

    public Double getUsefulArea() {
        return usefulArea;
    }

    public void setUsefulArea(Double usefulArea) {
        this.usefulArea = usefulArea;
    }

    public Double getCloud() {
        return cloud;
    }

    public void setCloud(Double cloud) {
        this.cloud = cloud;
    }

    public Double[] getElevation() {
        return elevation;
    }

    public void setElevation(Double[] elevation) {
        this.elevation = elevation;
    }

    public Double[] getOna() {
        return ona;
    }

    public void setOna(Double[] ona) {
        this.ona = ona;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Double[] getIncidence() {
        return incidence;
    }

    public void setIncidence(Double[] incidence) {
        this.incidence = incidence;
    }

    public Double[] getSza() {
        return sza;
    }

    public void setSza(Double[] sza) {
        this.sza = sza;
    }

    public Integer getStereo() {
        return stereo;
    }

    public void setStereo(Integer stereo) {
        this.stereo = stereo;
    }

    public String getPolarisation() {
        return polarisation;
    }

    public void setPolarisation(String polarisation) {
        this.polarisation = polarisation;
    }

}
