package com.geocento.webapps.eobroker.common.shared.feasibility;

import java.util.Date;

/**
 * Created by thomas on 02/06/2017.
 */
public class TimeSerie {

    Date date;
    Double value;
    String comment;

    public TimeSerie() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
