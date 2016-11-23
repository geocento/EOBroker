package com.geocento.webapps.eobroker.customer.client.utils;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.google.gwt.storage.client.Storage;

/**
 * Created by thomas on 03/06/2016.
 */
public class Utils {

    private static Storage stockStore = null;
    static {
        stockStore = Storage.getLocalStorageIfSupported();
    }

    private static AoIDTO aoIDTO = null;

    public static void saveAoI(AoIDTO aoi) {
        aoIDTO = aoi;
/*
        saveLocalStorage("aoiid", aoi.getId() + "");
        saveLocalStorage("aoi", AoIUtil.toWKT(aoi));
*/
    }

    private static void saveLocalStorage(String property, String value) {
        if(stockStore != null) {
            stockStore.setItem(property, value);
        }
    }

    private static String getLocalStorage(String property) {
        if(stockStore != null) {
            return stockStore.getItem(property);
        }
        return null;
    }

    public static AoIDTO getAoI() {
/*
        AoIDTO aoi = AoIUtil.fromWKT(getLocalStorage("aoi"));
        String aoiId = getLocalStorage("aoiId");
        aoi.setId(aoiId == null ? null : Long.valueOf(aoiId));
        return aoi;
*/
        return aoIDTO;
    }

}
