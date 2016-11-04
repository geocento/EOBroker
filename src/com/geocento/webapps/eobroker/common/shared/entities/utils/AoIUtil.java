package com.geocento.webapps.eobroker.common.shared.entities.utils;

import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.AoIPolygon;
import com.geocento.webapps.eobroker.common.shared.entities.AoIRectangle;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Created by thomas on 05/07/2016.
 */
public class AoIUtil {

    public static String toWKT(AoI aoi) {
        if(aoi instanceof AoIPolygon) {
            return "POLYGON((" + ((AoIPolygon) aoi).getWktRings() + "))";
        } else if(aoi instanceof AoIRectangle) {
            Extent extent = ((AoIRectangle) aoi).getExtent();
            return "POLYGON((" +
                    extent.getEast() + " " + extent.getNorth() + "," +
                    extent.getWest() + " " + extent.getNorth() + "," +
                    extent.getWest() + " " + extent.getSouth() + "," +
                    extent.getEast() + " " + extent.getSouth() + "," +
                    extent.getEast() + " " + extent.getNorth() +
                    "))";
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

    public static Extent extentFromWKT(String extent) throws ParseException {
        Geometry geometry = new WKTReader().read(extent);
        return getExtent(geometry);
    }

    private static Extent getExtent(Geometry geometry) {
        Extent extent = new Extent();
        Envelope envelop = geometry.getEnvelopeInternal();
        extent.setNorth(envelop.getMaxY());
        extent.setSouth(envelop.getMinY());
        extent.setEast(envelop.getMaxX());
        extent.setWest(envelop.getMinX());
        return extent;
    }
}
