package com.geocento.webapps.eobroker.common.shared.imageapi;

public enum RADAR_BANDS {
    All, LBand, SBand, CBand, XBand, KuBand;
    public RADAR_BANDS fromString(final String value) {
        return RADAR_BANDS.valueOf(value);
    }
};
