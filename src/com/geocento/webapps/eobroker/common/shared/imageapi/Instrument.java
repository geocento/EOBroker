package com.geocento.webapps.eobroker.common.shared.imageapi;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 *
 * A satellite instrument
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Instrument {

    String name;
    List<Mode> modes;
    String description;
    String informationUrl;
    boolean sampleImagery;
    String type;
    String band;
    double resolution;

    public Instrument() {
    }

    /**
     *
     * @return the name of the instrument
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return the modes of the instrument
     */
    public List<Mode> getModes() {
        return modes;
    }

    public void setModes(List<Mode> modes) {
        this.modes = modes;
    }

    /**
     *
     * @return a simple description of the instrument
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return a link to a more complete description of the instrument and its satellite
     */
    public String getInformationUrl() {
        return informationUrl;
    }

    public void setInformationUrl(String informationUrl) {
        this.informationUrl = informationUrl;
    }

    /**
     *
     * @return whether EarthImages can provide sample imagery for this instrument
     */
    public boolean isSampleImagery() {
        return sampleImagery;
    }

    public void setSampleImagery(boolean sampleImagery) {
        this.sampleImagery = sampleImagery;
    }

    /**
     *
     * @return the type (Optical or SAR)
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return the bands of this instrument
     */
    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    /**
     *
     * @return the maximum ground resolution for this instrument in meters
     */
    public double getResolution() {
        return resolution;
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
    }
}
