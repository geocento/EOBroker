package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by thomas on 10/03/2017.
 */
@Embeddable
public class TemporalCoverage {

    @Temporal(TemporalType.TIMESTAMP)
    Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date stopDate;

    Boolean refreshed;

    @Column(length = 100)
    String refreshRateDescription;

    public TemporalCoverage() {
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public Boolean getRefreshed() {
        return refreshed;
    }

    public void setRefreshed(Boolean refreshed) {
        this.refreshed = refreshed;
    }

    public String getRefreshRateDescription() {
        return refreshRateDescription;
    }

    public void setRefreshRateDescription(String refreshRateDescription) {
        this.refreshRateDescription = refreshRateDescription;
    }
}
