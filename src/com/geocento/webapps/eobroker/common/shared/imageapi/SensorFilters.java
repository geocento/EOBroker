package com.geocento.webapps.eobroker.common.shared.imageapi;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 *
 * A filter for selecting sensors
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class SensorFilters {

    double minResolution = 0;
    double maxResolution = 10000;

    SENSOR_TYPE type = SENSOR_TYPE.All;

    List<OPTICAL_WAVE_BANDS> opticalBands;

    List<RADAR_BANDS> radarBands;

    boolean includeFutureMissions = false;
/*
    boolean orderableOnly = false;
    Set<String> suppliers;
*/

    public SensorFilters() {
    }

    /**
     *
     * minimum resolution of imagery required
     *
     * @return
     */
    public double getMinResolution() {
        return minResolution;
    }

    public void setMinResolution(double minResolution) {
        this.minResolution = minResolution;
    }

    /**
     *
     * maxium resolution of imagery required
     *
     * @return
     */
    public double getMaxResolution() {
        return maxResolution;
    }

    public void setMaxResolution(double maxResolution) {
        this.maxResolution = maxResolution;
    }

    /**
     *
     * filter on type of sensor required, two values Optical or Radar
     *
     * @return
     */
    public SENSOR_TYPE getType() {
        return type;
    }

    public void setType(SENSOR_TYPE type) {
        this.type = type;
    }

    /**
     *
     * filter on optical bands required, values are oceanBlue, blue, green, red, redEdge, NIR, SWIR, MWIR, TIR
     *
     * @return (only optical)
     */
    public List<OPTICAL_WAVE_BANDS> getOpticalBands() {
        return opticalBands;
    }

    public void setOpticalBands(List<OPTICAL_WAVE_BANDS> opticalBands) {
        this.opticalBands = opticalBands;
    }

    /**
     *
     * (only Radar) filter on SAR bands required, values are LBand, SBand, CBand, XBand, KuBand
     *
     * @return
     */
    public List<RADAR_BANDS> getRadarBands() {
        return radarBands;
    }

    public void setRadarBands(List<RADAR_BANDS> radarBands) {
        this.radarBands = radarBands;
    }

    /**
     *
     * (only for future acquisitions) include missions that have not yet been launched
     *
     * @return
     */
    public boolean isIncludeFutureMissions() {
        return includeFutureMissions;
    }

    public void setIncludeFutureMissions(boolean includeFutureMissions) {
        this.includeFutureMissions = includeFutureMissions;
    }

/*
    public boolean isOrderableOnly() {
        return orderableOnly;
    }

    public void setOrderableOnly(boolean orderableOnly) {
        this.orderableOnly = orderableOnly;
    }

    public Set<String> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(Set<String> suppliers) {
        this.suppliers = suppliers;
    }
*/

    /** Deserializes an Object of class MyClass from its JSON representation */
/*
    public static SensorFilters valueOf(String jsonRepresentation) {
        ObjectMapper mapper = new ObjectMapper(); //Jackson's JSON marshaller
        SensorFilters sensorFilters = null;
        try {
            sensorFilters = mapper.readValue(jsonRepresentation, SensorFilters.class );
        } catch (IOException e) {
            throw new WebApplicationException();
        }
        return sensorFilters;
    }
*/

}
