package com.geocento.webapps.eobroker.common.server.Utils;

import com.geocento.webapps.eobroker.common.server.ServerUtil;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;

import java.net.MalformedURLException;

/**
 * Created by thomas on 10/01/2017.
 */
public class GeoserverUtils {

    public static GeoServerRESTPublisher getGeoserverPublisher() {
        return new GeoServerRESTPublisher(ServerUtil.getSettings().getGeoserverRESTUri(), ServerUtil.getSettings().getGeoserverUser(), ServerUtil.getSettings().getGeoserverPassword());
    }

    public static GeoServerRESTReader getGeoserverReader() throws MalformedURLException {
        return new GeoServerRESTReader(ServerUtil.getSettings().getGeoserverRESTUri(), ServerUtil.getSettings().getGeoserverUser(), ServerUtil.getSettings().getGeoserverPassword());
    }

}
