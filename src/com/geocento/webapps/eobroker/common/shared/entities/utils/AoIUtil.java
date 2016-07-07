package com.geocento.webapps.eobroker.common.shared.entities.utils;

import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.AoIPolygon;

/**
 * Created by thomas on 05/07/2016.
 */
public class AoIUtil {

    public static String toWKT(AoI aoi) {
        if(aoi instanceof AoIPolygon) {
            return "POLYGON((" + ((AoIPolygon) aoi).getWktRings() + "))";
        }
        return null;
    }
}
