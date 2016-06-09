package com.geocento.webapps.eobroker.shared.entities.dtos;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by thomas on 03/06/2016.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class AoIDTO {

    String wktValue;

    public AoIDTO() {
    }

    public String getWktValue() {
        return wktValue;
    }

    public void setWktValue(String wktValue) {
        this.wktValue = wktValue;
    }

}
