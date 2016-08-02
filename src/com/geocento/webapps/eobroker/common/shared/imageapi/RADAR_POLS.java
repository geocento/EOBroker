package com.geocento.webapps.eobroker.common.shared.imageapi;

public enum RADAR_POLS {
All, HH, HV, VH, VV;
public RADAR_POLS fromString(final String value) {
return RADAR_POLS.valueOf(value);
}
}
