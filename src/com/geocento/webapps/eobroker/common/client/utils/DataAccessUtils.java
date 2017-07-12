package com.geocento.webapps.eobroker.common.client.utils;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessFile;
import com.google.gwt.core.client.GWT;

/**
 * Created by thomas on 12/07/2017.
 */
public class DataAccessUtils {

    static public String getDownloadUrl(DatasetAccessFile datasetAccessFile) {
        String fileUri = datasetAccessFile.getUri();
        if (fileUri.startsWith("./")) {
            fileUri = GWT.getHostPageBaseURL() + "uploaded/" + fileUri;
        }
        return fileUri;
    }
}
