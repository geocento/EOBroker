package com.geocento.webapps.eobroker.common.shared.imageapi;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;
import java.util.List;

/**
 *
 * A satellite
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Satellite {

    String name;
    String description;
    String informationUrl;
    Date startMissionDate;
    Date stopMissionDate;
    List<Instrument> instruments;

    public Satellite() {
    }

    /**
     *
     * name of the satellite
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * a short description of the satellite
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * URL to a more detailed description of the satellite
     *
     * @return
     */
    public String getInformationUrl() {
        return informationUrl;
    }

    public void setInformationUrl(String informationUrl) {
        this.informationUrl = informationUrl;
    }

    /**
     *
     * the date of the start of the mission
     *
     * @return
     */
    public Date getStartMissionDate() {
        return startMissionDate;
    }

    public void setStartMissionDate(Date startMissionDate) {
        this.startMissionDate = startMissionDate;
    }

    /**
     *
     * the end of the mission or expected end of the mission
     *
     * @return
     */
    public Date getStopMissionDate() {
        return stopMissionDate;
    }

    public void setStopMissionDate(Date stopMissionDate) {
        this.stopMissionDate = stopMissionDate;
    }

    /**
     *
     * the list of instruments on board the satellite
     *
     * @return
     */
    public List<Instrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<Instrument> instruments) {
        this.instruments = instruments;
    }
}
