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
        return getExtent(aoi.getWktGeometry());
    }

    public static Extent getExtent(String wkt) {
        Extent extent = new Extent();
        extent.setEast(-180.0);
        extent.setSouth(90.0);
        extent.setWest(180.0);
        extent.setNorth(-90.0);
        wkt = wkt.substring(wkt.indexOf("((") + 2, wkt.indexOf("))"));
        for(String latLongString : wkt.split(",")) {
            String[] lngLat = latLongString.trim().split(" +");
            double lat = Double.parseDouble(lngLat[1]);
            double lng = Double.parseDouble(lngLat[0]);
            extent.setEast(Math.max(lng, extent.getEast()));
            extent.setWest(Math.min(lng, extent.getWest()));
            extent.setNorth(Math.max(lat, extent.getNorth()));
            extent.setSouth(Math.min(lat, extent.getSouth()));
        }
        return extent;
    }
}
