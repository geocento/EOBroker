package com.geocento.webapps.eobroker.common.shared.imageapi;

public enum OPTICAL_TYPE {
All, PAN, MULTI, HYPER;
public OPTICAL_TYPE fromString(final String value) {
return OPTICAL_TYPE.valueOf(value);
}
}
