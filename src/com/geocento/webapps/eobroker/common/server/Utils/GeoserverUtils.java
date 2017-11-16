package com.geocento.webapps.eobroker.common.server.Utils;

import com.geocento.webapps.eobroker.common.server.ServerUtil;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.shared.LayerInfoDTO;
import com.google.gwt.http.client.RequestException;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static boolean existsWorkpspace(String workspaceName) throws MalformedURLException {
        return getGeoserverReader().getWorkspaceNames().contains(workspaceName);
    }

}
