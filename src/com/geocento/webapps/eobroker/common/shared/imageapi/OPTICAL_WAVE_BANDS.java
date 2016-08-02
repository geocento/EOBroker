package com.geocento.webapps.eobroker.common.shared.imageapi;

public enum OPTICAL_WAVE_BANDS {
oceanBlue, blue, green, red, redEdge, NIR, SWIR, MWIR, TIR;
public OPTICAL_WAVE_BANDS fromString(final String value) {
return OPTICAL_WAVE_BANDS.valueOf(value);
}
}
