package com.geocento.webapps.eobroker.customer.shared.feasibility;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class TimeValue {
    Date date;
    Double value;

    public TimeValue() {
    }

    public TimeValue(Date date, Double value) {
        this.date = date;
        this.value = value;
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
}

