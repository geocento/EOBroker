package com.geocento.webapps.eobroker.common.shared.feasibility;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by thomas on 02/06/2017.
 */
public class TimePoint {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    Date date;
    String comment;

    public TimePoint() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
