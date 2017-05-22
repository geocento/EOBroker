package com.geocento.webapps.eobroker.common.shared.entities.datasets;

/**
 * Created by thomas on 17/05/2017.
 */
public class OSQueryRequest {
    private String aoiWKT;
    private long start;
    private long stop;

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    public String getAoiWKT() {
        return aoiWKT;
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
