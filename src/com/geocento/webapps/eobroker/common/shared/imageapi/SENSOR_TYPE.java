package com.geocento.webapps.eobroker.common.shared.imageapi;

public enum SENSOR_TYPE {
    All, Optical, Radar;
    public SENSOR_TYPE fromString(final String value) {
        return SENSOR_TYPE.valueOf(value);
    }
};
