package com.geocento.webapps.eobroker.common.shared.entities;

/**
 * Created by thomas on 26/08/2016.
 */
public class SearchQuery {
    private String aoiWKT;
    private String sensors;
    private long start;
    private long stop;

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setSensors(String sensors) {
        this.sensors = sensors;
    }

    public String getSensors() {
        return sensors;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStart() {
        return start;
    }

    public void setStop(long stop) {
        this.stop = stop;
    }

    public long getStop() {
        return stop;
    }
}
