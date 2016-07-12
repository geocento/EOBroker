package com.geocento.webapps.eobroker.common.client.widgets.maps;

import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.GeometryJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.PolygonJSNI;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.AoIPolygon;

/**
 * Created by thomas on 03/06/2016.
 */
public class AoIUtil {

    public static AoI createAoI(GeometryJSNI geometry) {
        switch(geometry.getType()) {
            case "polygon":
                AoIPolygon aoiPolygon = new AoIPolygon();
                aoiPolygon.setWktRings(((PolygonJSNI) geometry).getRings());
                return aoiPolygon;
        }
        return null;
    }

    public static String toWKT(AoI aoi) {
        if(aoi instanceof AoIPolygon) {
            return "POLYGON((" + ((AoIPolygon) aoi).getWktRings() + "))";
        }
        return null;
    }
}
