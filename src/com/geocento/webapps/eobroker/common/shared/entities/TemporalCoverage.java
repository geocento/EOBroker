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

    Integer refreshRate;

    @Enumerated(EnumType.STRING)
    DATE_UNIT dateUnit;

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

    public Integer getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(Integer refreshRate) {
        this.refreshRate = refreshRate;
    }

    public DATE_UNIT getDateUnit() {
        return dateUnit;
    }

    public void setDateUnit(DATE_UNIT dateUnit) {
        this.dateUnit = dateUnit;
    }
}
