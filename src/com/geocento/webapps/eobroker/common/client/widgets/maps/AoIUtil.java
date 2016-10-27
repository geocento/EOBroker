package com.geocento.webapps.eobroker.common.client.widgets.maps;

import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.GeometryJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.PolygonJSNI;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.AoIPolygon;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;

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

    public static AoI fromWKT(String aoiWKT) {
        if(aoiWKT == null) {
            return null;
        }
        if(aoiWKT.startsWith("POLYGON((")) {
            AoIPolygon aoIPolygon = new AoIPolygon();
            aoIPolygon.setWktRings(aoiWKT.replace("POLYGON((", "").replace("))", ""));
            return aoIPolygon;
        }
        return null;
    }

    public static Extent getExtent(AoI aoi) {
        return null;
    }
}
