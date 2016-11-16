package com.geocento.webapps.eobroker.common.client.widgets.maps;

import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.GeometryJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.PolygonJSNI;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;

/**
 * Created by thomas on 03/06/2016.
 */
public class AoIUtil {

    public static AoIDTO createAoI(GeometryJSNI geometry) {
        AoIDTO aoIDTO = new AoIDTO();
        switch(geometry.getType()) {
            case "polygon":
                aoIDTO.setWktGeometry("POLYGON((" + ((PolygonJSNI) geometry).getRings() + "))");
                return aoIDTO;
        }
        return null;
    }

    public static String toWKT(AoIDTO aoi) {
        return aoi.getWktGeometry();
    }

    public static AoIDTO fromWKT(String aoiWKT) {
        if(aoiWKT == null) {
            return null;
        }
        AoIDTO aoIDTO = new AoIDTO();
        aoIDTO.setWktGeometry(aoiWKT);
        return aoIDTO;
    }

    public static Extent getExtent(AoIDTO aoi) {
        return null;
    }
}
