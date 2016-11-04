package com.geocento.webapps.eobroker.common.server.Utils;

import org.postgis.PGgeometry;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by thomas on 04/11/2016.
 */
@Converter(autoApply = false)
public class GeometryConverter implements AttributeConverter<String, PGgeometry> {

    @Override
    public PGgeometry convertToDatabaseColumn(String wktString) {
        if(wktString != null && wktString.length() > 0) {
            try {
                return new PGgeometry(wktString);
                        //WKBWriter.toHex(new WKBWriter().write(new WKTReader().read(wktString)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String convertToEntityAttribute(PGgeometry pGgeometry) {
        if(pGgeometry != null) {
            return pGgeometry.getValue();
/*
            byte[] aux = WKBReader.hexToBytes(pGgeometry);
            Geometry geom = null;
            try {
                geom = new WKBReader().read(aux);
                return geom.toText();
            } catch (ParseException e) {
                e.printStackTrace();
            }
*/
        }
        return null;
    }
}
