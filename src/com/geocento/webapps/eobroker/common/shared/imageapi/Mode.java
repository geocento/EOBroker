package com.geocento.webapps.eobroker.common.shared.imageapi;

/**
 *
 * An instrument mode
 */
public class Mode {

    String name;
    double minAngle = 0;
    double maxAngle = 0;

    public Mode() {
    }

    /**
     * name of the mode
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * min angle, used to calculate the aperture
     *
     * @return
     */
    public double getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(double minAngle) {
        this.minAngle = minAngle;
    }

    /**
     * max angle, used to calculate the aperture
     *
     * @return
     */
    public double getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(double maxAngle) {
        this.maxAngle = maxAngle;
    }
}
