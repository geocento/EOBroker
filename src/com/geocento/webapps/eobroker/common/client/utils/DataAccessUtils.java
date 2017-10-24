package com.geocento.webapps.eobroker.common.client.utils;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.google.gwt.core.client.GWT;

/**
 * Created by thomas on 12/07/2017.
 */
public class DataAccessUtils {

    static public String getDownloadUrl(DatasetAccess datasetAccess) {
        String fileUri = datasetAccess.getUri();
        if(datasetAccess.isHostedData()) {
            return fileUri;
        }
        return GWT.getHostPageBaseURL() + "customer/api/download-data/download/" + datasetAccess.getId();
    }
}
