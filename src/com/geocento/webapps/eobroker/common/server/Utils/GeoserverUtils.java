package com.geocento.webapps.eobroker.common.server.Utils;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;

import java.net.MalformedURLException;

/**
 * Created by thomas on 10/01/2017.
 */
public class GeoserverUtils {

    static String RESTURL  = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.geoserverRESTUri);
    static String RESTUSER = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.geoserverUser);
    static String RESTPW   = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.geoserverPassword);

    public static GeoServerRESTPublisher getGeoserverPublisher() {
        return new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);
    }

    public static GeoServerRESTReader getGeoserverReader() throws MalformedURLException {
        return new GeoServerRESTReader(RESTURL, RESTUSER, RESTPW);
    }

}
