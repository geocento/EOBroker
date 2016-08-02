package com.geocento.webapps.eobroker.common.shared.imageapi;

public enum RADAR_POLS_TYPES {
All, Single, Dual, Full;
public RADAR_POLS_TYPES fromString(final String value) {
return RADAR_POLS_TYPES.valueOf(value);
}
}
